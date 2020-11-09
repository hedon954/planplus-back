package com.hedon.filter;

import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Hedon Wang
 * @create 2020-10-31 15:48
 */
public class ZuulAuditLogFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //通过安全上下文来解析令牌并获取当前用户的用户名
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        //TODO:请求进来时审计处理（这里简化输出，需要根据实际需求做审计处理，如存储到数据库等）
        if (!request.getRequestURI().equals("/actuator/health")){
            System.out.println("① 请求 " + request.getRequestURI() +"进来了，开始为用户 "+ username + " 做审计");
        }
        //执行其他的过滤器
        filterChain.doFilter(request,response);
        //TODO:请求完成后的审计处理：将请求的失败或者成功的结果更新到数据库里。整个过滤链执行完毕后就会执行下面的语句
        //判断日志是否被修改过了
        if(StringUtils.isBlank((String)request.getAttribute("logUpdated")) && !request.getRequestURI().equals("/actuator/health")){
            //如果不存 logUpdated 字段，那就说明没有被修改过，说明没有异常
            System.out.println("③ 更新日志结果为 -> 处理成功");
        }
    }
}
