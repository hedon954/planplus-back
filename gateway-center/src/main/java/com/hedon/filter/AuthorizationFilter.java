package com.hedon.filter;

import cn.hutool.json.JSONUtil;
import com.hedon.bean.CheckTokenInfo;
import common.code.ResultCode;
import common.entity.DidaUser;
import common.entity.User;
import common.mapper.DidaUserMapper;
import common.mapper.UserMapper;
import common.vo.common.ResponseBean;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import java.nio.charset.StandardCharsets;

/**
 * ③ 授权过滤器
 *
 * @author Hedon Wang
 * @create 2020-10-16 20:13
 */
@Component
@Slf4j
public class AuthorizationFilter implements GlobalFilter, Ordered {

    @Autowired
    private DidaUserMapper didaUserMapper;

    /**
     * 授权逻辑
     * @param exchange
     * @param chain
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        System.out.println("授权开始 ==============>>>>>>>>>>>>");

        //获取 request，response
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        //获取请求路径
        String path = request.getURI().getPath();

        /**
         * ============================================
         *                  下面开始授权
         * ============================================
         */
        //① 该请求是否需要权限
        if (UrlFilter.isNeedAuthentication(path)){
            //② 需要认证，那就拿出 tokenInfo
            CheckTokenInfo tokenInfo = (CheckTokenInfo) exchange.getAttributes().get("tokenInfo");
            //③ tokenInfo 是否存在，存在是否可用
            if (tokenInfo !=null && tokenInfo.isActive()){
                //④ 认证成功（能拿到用户信息且有效） => 是否有权限
                if (hasPermissions(tokenInfo,request)){
                    //有权限就放行, 并将用户信息放在 requestHeader 中，让接口可以获取到用户相关的信息
                    DidaUser user = didaUserMapper.getUserByPhone(tokenInfo.getUser_name());
                    exchange.getAttributes().put("user", user);
                }else{
                    //没有权限，记录审计日志
                    //TODO:这里应该做审计日志处理
                    log.info(path + ":" + request.getMethod().name() + "异常审计===============>>>>>403 错误，授权失败....");
                    //处理异常
                    return handlerError(HttpStatus.FORBIDDEN,response,ResultCode.INSUFFICIENT_PERMISSION);
                }
            }else{
                /**
                 * tokenInfo 不存在或者不可用情况
                 *      不存在：没传 tokenInfo
                 *      不可用：token 过期或无效
                 */
                //TODO:这里应该做审计日志处理
                log.info(path + ":" + request.getMethod().name() + "异常审计=============>>>>>401 错误，认证失败...");
                //处理异常
                return handlerError(HttpStatus.FORBIDDEN,response,ResultCode.ERROR_TOKEN_VALUE);
            }
        }

        /**
         * 进到下一个过滤器，2种情况：
         *
         *  1. 请求不需要认证授权
         *  2. 到 ④ 后检查到用户是有权限的，就正常放行
         *
         */
        return chain.filter(exchange);
    }

    /**
     * 处理异常
     *
     * @param httpStatus   设置Http状态码
     * @param response     响应图
     * @param resultCode   返回码
     */
    private Mono<Void> handlerError(HttpStatus httpStatus, ServerHttpResponse response,ResultCode resultCode) {
        response.setStatusCode(httpStatus);
        response.setRawStatusCode(httpStatus.value());
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        String fail = JSONUtil.parse(ResponseBean.fail(resultCode)).toString();
        return response.writeWith(Mono.just(response.bufferFactory().wrap(fail.getBytes(StandardCharsets.UTF_8))));
    }

    /**
     * 认证成功后，判断用户是否拥有权限
     *
     * @param tokenInfo
     * @param request
     * @return
     */
    private boolean hasPermissions(CheckTokenInfo tokenInfo, ServerHttpRequest request) {

        //TODO:这里需要根据项目需求来确定什么样的请求需要什么样的权限，这里先假定 GET 方法需要 read 权限，其他的需要 write 权限

        //获取用户拥有的权限
        String[] scopes = tokenInfo.getScope();
        //获取请求方式
        HttpMethod method = request.getMethod();
        if (method.matches("GET")){
            //GET 请求需要 read 权限
            for (String scope: scopes){
                if (StringUtils.contains(scope,"read")){
                    return true;
                }
            }
        }else{
            //其他请求如 POST、PUT、DELETE 需要 write 权限
            for (String scope: scopes){
                if (StringUtils.contains(scope,"write")){
                    return true;
                }
            }
        }
        //没有权限
        return false;
    }


    /**
     * 过滤器的顺序：限流(用Hystrix)->认证(1)->审计(2)->授权(3)
     *
     * @return
     */
    @Override
    public int getOrder() {
        return 3;
    }
}
