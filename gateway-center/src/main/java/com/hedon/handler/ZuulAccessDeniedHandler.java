package com.hedon.handler;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 403 异常处理器
 *
 * @author Hedon Wang
 * @create 2020-10-31 16:23
 */
@Component
public class ZuulAccessDeniedHandler extends OAuth2AccessDeniedHandler {

    /**
     * 处理 403 错误
     */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException authException) throws IOException, ServletException {

        //TODO:根据具体业务需求可以利用 request、response、authException 里面的信息来处理 403 错误

        //这里为了方便，就简单输出了
        System.out.println("② 更改日志结果为 ->  请求报了 403 错误");

        //加一个标志，告诉审计过滤器日志已经被修改过了，不需要再修改了
        request.setAttribute("logUpdated","yes");

        //调用父类的方法，返回默认的提醒信息
        super.handle(request, response, authException);
    }
}
