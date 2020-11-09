package com.hedon.filter;

import cn.hutool.json.JSONUtil;
import com.google.common.util.concurrent.RateLimiter;
import common.code.ResultCode;
import common.vo.common.ResponseBean;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 限流过滤器
 *
 * @author Hedon Wang
 * @create 2020-10-31 17:16
 */
public class ZuulRateLimitFilter extends OncePerRequestFilter {

    /**
     * 限流器，参数的每秒可以接受的请求量
     */
    private RateLimiter rateLimiter = RateLimiter.create(10);

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {

        if (rateLimiter.tryAcquire()){
            filterChain.doFilter(httpServletRequest,httpServletResponse);
        }else {
            httpServletResponse.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            httpServletResponse.setContentType("application/json");
            ResponseBean tooManyRequest = ResponseBean.fail(ResultCode.TO_MANY_REQUESTS);
            httpServletResponse.getWriter().write(JSONUtil.parse(tooManyRequest).toString());
            httpServletResponse.getWriter().flush();
            return;
        }
    }
}
