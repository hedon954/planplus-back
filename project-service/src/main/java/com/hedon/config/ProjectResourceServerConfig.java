package com.hedon.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;

/**
 * @author Hedon Wang
 * @create 2020-11-02 16:11
 */
@Configuration
@EnableResourceServer
public class ProjectResourceServerConfig extends ResourceServerConfigurerAdapter {


    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources
                .resourceId("project-service");
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/oauth/**",
                        "/project/login/login",
                        "/actuator/**",
                        "/instances",
                        "/instances/**",
                        "/project/login/register",
                        "/project/code/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .httpBasic();
    }
}
