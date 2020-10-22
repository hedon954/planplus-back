package com.hedon.feign;

import common.vo.common.ResponseBean;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;


/**
 * @author Hedon Wang
 * @create 2020-10-17 10:31
 */
@FeignClient(value = "auth-center")  //指明要去调用注册中心的哪个微服务的接口
public interface UserFeignService {

    /**
     * 获取所有用户信息
     *
     * @return 所有用户信息
     */
    @GetMapping("/oauth/user/users")
    ResponseBean getUsers();

}
