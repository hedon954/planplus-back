package com.hedon.config;

import com.hedon.filter.ZuulAuditLogFilter;
import com.hedon.filter.ZuulRateLimitFilter;
import com.hedon.handler.ZuulAccessDeniedHandler;
import com.hedon.handler.ZuulAuthenticationEntryPoint;
import com.hedon.handler.ZuulWebSecurityExpressionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;

/**
 * @author Hedon Wang
 * @create 2020-10-31 01:08
 */
@Configuration
@EnableResourceServer  //作为资源服务器
public class ZuulResourceSecurityConfig extends ResourceServerConfigurerAdapter {

    @Autowired
    private ZuulWebSecurityExpressionHandler zuulWebSecurityExpressionHandler;

    @Autowired
    private ZuulAccessDeniedHandler zuulAccessDeniedHandler;

    @Autowired
    private ZuulAuthenticationEntryPoint zuulAuthenticationEntryPoint;

    /**
     * 配置资源服务器
     */
    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources
                .authenticationEntryPoint(zuulAuthenticationEntryPoint) //401 处理器
                .accessDeniedHandler(zuulAccessDeniedHandler)           //403 处理器
                .expressionHandler(zuulWebSecurityExpressionHandler)    //注册表达式处理器
                .resourceId("gateway");
    }



    /**
     * 访问权限控制
     */
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .addFilterBefore(new ZuulRateLimitFilter(), ChannelProcessingFilter.class)   //插入限流过滤器
                .addFilterBefore(new ZuulAuditLogFilter(), ExceptionTranslationFilter.class)  //插入审计过滤器
                .authorizeRequests()
                .antMatchers("/oauth/**",
                        "/project/login/login",
                        "/actuator/**",
                        "/instances",
                        "/instances/**",
                        "/project/login/register",
                        "/project/code/**",
                        "/project/login/getPasswordBack").permitAll()
                .anyRequest().access("#permissionService.hasPermission(request,authentication)");
    }
}
