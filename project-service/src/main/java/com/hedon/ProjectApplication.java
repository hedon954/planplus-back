package com.hedon;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author Hedon Wang
 * @create 2020-10-16 00:06
 */
@SpringBootApplication
@EnableDiscoveryClient                                   //注册进 consul
@EnableSwagger2                                          //支持 Swagger2x
@MapperScan(value = "common.mapper")                     //扫描 mapper 文件
@EnableFeignClients(basePackages = "com.hedon.feign")    //支持 openFeign
@EnableHystrix                                           //支持 Hystrix
@EnableCircuitBreaker                                    //启用断路器，支持服务熔断、降级
@EnableTransactionManagement                             //支持事务管理
@EnableZuulProxy                                         //支持 Zuul 网关代理
@EnableAsync                                             //支持异步请求
public class ProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProjectApplication.class,args);
    }

    /**
     * 密码加密器
     */
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    /**
     * 定义 OAuth2RestTemplate
     * @param resource   资源信息
     * @param context    请求上下文（就是从这个里面拿到 token，并自动帮我们放到 header 中的）
     * @return
     */
    @Bean
    public OAuth2RestTemplate oAuth2RestTemplate(OAuth2ProtectedResourceDetails resource, OAuth2ClientContext context){
        return new OAuth2RestTemplate(resource,context);
    }




}
