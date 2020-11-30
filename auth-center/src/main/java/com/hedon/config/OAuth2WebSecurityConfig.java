package com.hedon.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author Hedon Wang
 * @create 2020-10-16 17:02
 */
@Configuration
@EnableWebSecurity  //开启网络安全配置
public class OAuth2WebSecurityConfig extends WebSecurityConfigurerAdapter {


    @Autowired
    private UserDetailsService userDetailsService;


    @Autowired
    private PasswordEncoder passwordEncoder;



    //配置如何构建 AuthenticationManager
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userDetailsService)   //用户详细信息（用户名、权限等）
                .passwordEncoder(passwordEncoder);        //密码加密器
    }

    //将 AuthenticationManager 注入到 ioc 容器中，给 OAuth2AuthServerConfig 使用
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/project/img/**",
                        "/project/login/login",
                        "/oauth/**",
                        "/actuator/**",
                        "/instances", "/instances/**",
                        "/project/login/register",
                        "/project/code/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .httpBasic();
    }

}
