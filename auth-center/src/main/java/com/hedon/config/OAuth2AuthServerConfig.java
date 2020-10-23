package com.hedon.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

import javax.sql.DataSource;

/**
 * @author Hedon Wang
 * @create 2020-10-16 16:52
 */

@Configuration
@EnableAuthorizationServer  //作为认证服务器
public class OAuth2AuthServerConfig extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private AuthenticationManager authenticationManager;

    //注入数据源
    @Autowired
    private DataSource dataSource;

    //token存储器
    @Bean
    public TokenStore tokenStore(){
        return new JdbcTokenStore(dataSource);
    }

    //配置客户端应用的相关信息
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.jdbc(dataSource);
    }

    //配置令牌管理器 => ① 管理访问端点；② 管理令牌服务（哪种类型的令牌、令牌中的用户信息、密码加密模式）
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints
                .tokenStore(tokenStore())  //配置令牌存储模式 =》 存储到数据库oauth_client_token表中
                .authenticationManager(authenticationManager);
    }


    //配置令牌端点的安全约束 => 配置谁能来验 token （有一些请求连验 token 的资格都没有）
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        //这里设置必须带身份信息来验证token才进行验证
        security.checkTokenAccess("isAuthenticated()");
    }
}
