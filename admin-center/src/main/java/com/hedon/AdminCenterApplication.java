package com.hedon;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author Hedon Wang
 * @create 2020-10-15 15:56
 */
@SpringBootApplication
@EnableDiscoveryClient   //注册进 Consul
@EnableAdminServer       //作为服务监控中心
public class AdminCenterApplication {
    public static void main(String[] args) {
        SpringApplication.run(AdminCenterApplication.class,args);
    }
}
