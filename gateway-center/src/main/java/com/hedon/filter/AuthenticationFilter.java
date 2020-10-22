package com.hedon.filter;

import com.hedon.bean.CheckTokenInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.*;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


/**
 * ① 认证过滤器
 *
 * @author Hedon Wang
 * @create 2020-10-16 20:11
 */
@Component
@Slf4j
public class AuthenticationFilter implements GlobalFilter, Ordered {

    /**
     * token 类型
     */
    public static final String BEARER = "bearer ";

    /**
     * token 对应的请求头
     */
    public static final String AUTHORIZATION = HttpHeaders.AUTHORIZATION;

    /**
     * 发送请求的工具类
     */
    private RestTemplate restTemplate = new RestTemplate();

    /**
     * 认证逻辑
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        System.out.println("认证开始 ==============>>>>>>>>>>>>");

        //获取 request
        ServerHttpRequest request = exchange.getRequest();

        /**
         * ============================================
         *                  下面开始认证
         * ============================================
         */

        //① 判断请求是否需要认证
        String path = request.getURI().getPath();
        System.out.println("① 判断请求是否需要认证 path :"+path);
        if (!UrlFilter.isNeedAuthentication(path)){
            //不需要认证的话直接放行
            return chain.filter(exchange);
        }
        //② 需要认证的话看是否携带认证信息
        String token = tryGetToken(request);
        if (!StringUtils.isNotBlank(token)){
            //如果没有 token，不放行
            //没带信息，往下走，这只是为了审计，后面肯定是走不到底的
            return chain.filter(exchange);
        }
        //③ 判断带的认证信息类型是否正确 => 需要以  bearer 开头
        if (!StringUtils.startsWith(token,BEARER)){
            //OAuth2.0 是 bearer 类型的，如果不是，那也继续往下走，然后审计，后面肯定也是走不到底的
            return chain.filter(exchange);
        }
        //④ 类型正确的话解析后看看是否有权限
        try{
            //解析令牌信息并放到我们自定义的封装类 CheckTokenInfo 中
            CheckTokenInfo info = getCheckTokenInfo(token);
            //获取到正常的 CheckTokenInfo 对象的话，就放到请求域中
            exchange.getAttributes().put("tokenInfo",info);
        }catch (Exception e){
            //TODO 这里进行日志记录，记录获取 tokenInfo 异常，现在先不记录，只做简单输出
            log.info(path + ":" + request.getMethod().name()+ "Get token info failed" );
        }
        return chain.filter(exchange);
    }

    /**
     * 解析令牌信息并放到我们自定义的封装类 CheckTokenInfo 中
     * @param token
     * @return
     */
    private CheckTokenInfo getCheckTokenInfo(String token) {
        //去掉 token 的前缀 bearer
        token = StringUtils.substringAfter(token,BEARER);
        //认证服务器检查 token 的地址
        String oauthServiceCheckTokenUrl = "http://localhost:9527/oauth/check_token";
        //封装请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        //网关也是一个客户端，我们也需要将它注册到认证服务器中
        headers.setBasicAuth("gateway","123456");
        //封装请求实体
        MultiValueMap<String,String> params = new LinkedMultiValueMap<>();
        params.add("token",token);
        HttpEntity<MultiValueMap<String,String>> entity = new HttpEntity<>(params,headers);
        /**
         * 发送请求
         *
         *  参数1：请求连接
         *  参数2：请求类型
         *  参数3：请求数据
         *  参数4：请求结果封装到哪里
         */
        ResponseEntity<CheckTokenInfo> response = restTemplate.exchange(oauthServiceCheckTokenUrl, HttpMethod.POST, entity, CheckTokenInfo.class);
        //拿到响应体
        CheckTokenInfo checkTokenInfo = response.getBody();
        return checkTokenInfo;
    }


    /**
     * 尝试从 request 中获取 token
     * @param request
     * @return
     */
    private String tryGetToken(ServerHttpRequest request) {
        //1. 尝试从请求参数中获取 token
        String token = request.getQueryParams().getFirst(AUTHORIZATION);
        //2. 如果参数中没有，那就尝试从请求头中获取 token
        if (!StringUtils.isNotBlank(token)){
            token = request.getHeaders().getFirst(AUTHORIZATION);
        }
        //3. 如果请求头中还是没有 token，那就尝试从 cookie 中获取 token
        if (!StringUtils.isNotBlank(token)){
            HttpCookie cookie = request.getCookies().getFirst(AUTHORIZATION);
            if (cookie != null){
                token = cookie.getValue();
            }
        }
        return token;
    }


    /**
     * 过滤器的顺序：限流(用Hystrix)->认证(1)->审计(2)->授权(3)
     * @return
     */
    @Override
    public int getOrder() {
        return 1;
    }

}
