package com.hedon.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试能否读到配置中心的配置信息 => 已测试通过
 *
 * @author Hedon Wang
 * @create 2020-10-16 16:02
 */
//@RestController
//@RefreshScope
//@RequestMapping("/project")
public class ConfigClientTestController {

    @Value("${server.port}")
    private String serverPort;

    @Value("${config.info}")
    private String configInfo;

    @GetMapping("/configInfo")
    public String getConfigInfo(){
        return "Server port: " + serverPort + "; configInfo: "+ configInfo;
    }

}
