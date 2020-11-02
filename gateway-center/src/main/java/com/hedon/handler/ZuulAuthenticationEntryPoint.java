package com.hedon.handler;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.http.AccessTokenRequiredException;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 401 处理器
 *
 * @author Hedon Wang
 * @create 2020-10-31 16:38
 */
@Component
public class ZuulAuthenticationEntryPoint extends OAuth2AuthenticationEntryPoint {

    /**
     *  处理 401 错误
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

        //TODO:根据具体业务需求可以利用 request、response、authException 里面的信息来处理 401 错误

        //这里为了方便，就简单输出了
        if (authException instanceof AccessTokenRequiredException){
            //如果是 AccessTokenRequiredException 说明是没带 token
            System.out.println("② 更改日志结果为 ->  请求报了 401 错误 -> 原因是没带令牌");
        }else{
            //其他的异常说明 token 无效
            System.out.println("添加日志 -> 日志结果为 ->  请求报了 401 错误 -> 原因是令牌无效");
        }

        //加一个标志，告诉审计过滤器日志已经被修改过了，不需要再修改了
        request.setAttribute("logUpdated","yes");

        //调用父类的方法，返回默认的提醒信息
        super.commence(request, response, authException);
    }
}
