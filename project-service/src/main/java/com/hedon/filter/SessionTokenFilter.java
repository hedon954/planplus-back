package com.hedon.filter;

import cn.hutool.json.JSONUtil;
import com.hedon.sercurity.TokenInfo;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import common.code.ResultCode;
import common.vo.common.ResponseBean;
import lombok.SneakyThrows;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;

/**
 * 基于 Session 的 SSO
 *
 * @author Hedon Wang
 * @create 2020-10-28 11:29
 */
@Component
public class SessionTokenFilter extends ZuulFilter {

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    private RestTemplate restTemplate = new RestTemplate();


    /**
     * 从 session 中拿到 token 并加到请求头
     * @return
     * @throws ZuulException
     */
    @SneakyThrows
    @Override
    public Object run() throws ZuulException {

        //拿到请求
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();
        //从请求中拿到 tokenInfo
        TokenInfo token = (TokenInfo) request.getSession().getAttribute("token");
        System.out.println("这是 SessionTokenFilter ：当前的 token " + token);
        //检查是否为空
        if (token!=null){
            if (token.isExpired()){
                /**
                 * 过期的话，发送请求去认证服务器刷新令牌
                 */
                //请求链接
                String refreshTokenUrl = "http://localhost:9527/oauth/token";
                //请求头
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                httpHeaders.setBasicAuth("planplus","123456");
                //请求参数
                MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
                params.add("grant_type","refresh_token");           //类型为刷新令牌类型
                params.add("refresh_token",token.getRefresh_token());  //刷新令牌
                //请求头
                HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params,httpHeaders);
                try{
                    //发送请求
                    ResponseEntity<TokenInfo> response = restTemplate.exchange(refreshTokenUrl, HttpMethod.POST, entity, TokenInfo.class);
                    //拿到新的 token，同时初始化它，设置过期时间
                    token = response.getBody().init();
                    //放到 session 中
                    request.getSession().setAttribute("token",token);
                }catch (Exception e){
                    /**
                     * 前面已经检测到 access_token 过期了，然后刷新令牌又异常了，那肯定就是令牌失效了
                     */
                    //不能再继续往下传了，得重新认证了
                    requestContext.setSendZuulResponse(false);
                    //返回错误码10086
                    requestContext.setResponseStatusCode(10086);
                    //返回错误信息
                    ResponseBean fail = ResponseBean.fail(ResultCode.INVALID_REFRESH_TOKEN);
                    requestContext.setResponseBody(JSONUtil.parse(fail).toString());
                    //设置响应类型为json
                    requestContext.getResponse().setContentType("application/json");
                    //清除 session
                    request.getSession().invalidate();
                }

            }
            //将 token 放在请求头中传下去
            requestContext.addZuulRequestHeader("Authorization","bearer "+token.getAccess_token());
        }
        return null;
    }

}
