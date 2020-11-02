package com.hedon.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationManager;
import org.springframework.security.oauth2.provider.token.*;

/**
 * @author Hedon Wang
 * @create 2020-11-02 19:08
 */
@Configuration
@EnableWebSecurity
public class ProjectWebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    @Qualifier("userDetailServiceImpl")
    private UserDetailsService userDetailsService;

    private AccessTokenConverter getAccessTokenConverter(){
        DefaultAccessTokenConverter defaultAccessTokenConverter = new DefaultAccessTokenConverter();
        DefaultUserAuthenticationConverter userTokenConverter = new DefaultUserAuthenticationConverter();
        userTokenConverter.setUserDetailsService(userDetailsService);
        defaultAccessTokenConverter.setUserTokenConverter(userTokenConverter);
        return defaultAccessTokenConverter;
    }

    //发送远程请求去请求认证服务器来进行认证
    @Bean
    public ResourceServerTokenServices tokenServices(){
        RemoteTokenServices remoteTokenServices =  new RemoteTokenServices();
        remoteTokenServices.setClientId("planplus");
        remoteTokenServices.setClientSecret("123456");
        remoteTokenServices.setCheckTokenEndpointUrl("http://localhost:9527/oauth/check_token"); //验证token的请求
        //把令牌转换为用户信息
        remoteTokenServices.setAccessTokenConverter(getAccessTokenConverter());
        return remoteTokenServices;
    }

    //用上面的服务去验证token
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        OAuth2AuthenticationManager auth2AuthenticationManager = new OAuth2AuthenticationManager();
        auth2AuthenticationManager.setTokenServices(tokenServices());
        return auth2AuthenticationManager;
    }
}
