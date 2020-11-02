package com.hedon.config;

import cn.hutool.json.JSONObject;
import com.hedon.service.DidaUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.session.jdbc.config.annotation.web.http.EnableJdbcHttpSession;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Hedon Wang
 * @create 2020-10-16 16:52
 */

@Configuration
@EnableAuthorizationServer  //作为认证服务器
@EnableJdbcHttpSession      //session持久化
public class OAuth2AuthServerConfig extends AuthorizationServerConfigurerAdapter {

    
    @Autowired
    private DidaUserService didaUserService;
    
    @Autowired
    @Qualifier("userDetailServiceImpl")
    private UserDetailsService userDetailsService;

    @Autowired
    private AuthenticationManager authenticationManager;

    //注入数据源
    @Autowired
    private DataSource dataSource;

    //token存储器
    @Bean
    public TokenStore tokenStore(){
        return new JwtTokenStore(jwtTokenEnhancer());
    }

    //jwt token 增强器
    @Bean
    public JwtAccessTokenConverter jwtTokenEnhancer(){
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter(){
            /*//令牌增强
            @Override
            public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
                System.out.println("正在增强令牌");
                //用户手机号
                UserDetails userDetails = (UserDetails)authentication.getPrincipal();
                String phoneNumber = userDetails.getUsername();
                //用户ID
                Integer userId = didaUserService.getUserIdByUserPhoneNumber(phoneNumber);
                //封装数据
                final Map<String,Object> addToJWTInfo = new HashMap<>();
                addToJWTInfo.put("user_id",userId);
                //存到令牌中
                DefaultOAuth2AccessToken accessToken1 = (DefaultOAuth2AccessToken) accessToken;
                accessToken1.setAdditionalInformation(addToJWTInfo);
                OAuth2AccessToken enhance = super.enhance(accessToken1, authentication);
                return enhance;
            }*/
        };
        jwtAccessTokenConverter.setSigningKey("planplus");
        return jwtAccessTokenConverter;
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
                .userDetailsService(userDetailsService)     //专门给 refresh_token 配置的 userDetailsService
                .tokenStore(tokenStore())                   //配置令牌存储模式 =》 存储到数据库oauth_client_token表中
                .tokenEnhancer(jwtTokenEnhancer())          //配置 JWT 令牌增强器
                .authenticationManager(authenticationManager);
    }


    //配置令牌端点的安全约束 => 配置谁能来验 token （有一些请求连验 token 的资格都没有）
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security
                //暴露获取 signingkey 的服务
                .tokenKeyAccess("isAuthenticated()")
                //这里设置必须带身份信息来验证token才进行验证
                .checkTokenAccess("isAuthenticated()");
    }
}
