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
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author Hedon Wang
 * @create 2020-11-03 19:14
 */
@SpringBootApplication
@EnableDiscoveryClient                                   //注册进 Consul
@EnableHystrix                                           //支持 Hystrix
@EnableSwagger2                                          //支持 Swagger2x
@EnableCircuitBreaker                                    //启用断路器，支持服务熔断、降级
@EnableTransactionManagement                             //支持事务
@MapperScan(value = "common.mapper")                     //扫描 mapper 文件
@EnableScheduling                                        //支持定时任务
public class NotificationApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotificationApplication.class,args);
    }

}
