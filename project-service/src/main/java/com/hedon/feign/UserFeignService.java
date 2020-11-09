package com.hedon.feign;

import org.springframework.cloud.openfeign.FeignClient;


/**
 * @author Hedon Wang
 * @create 2020-10-17 10:31
 */
@FeignClient(value = "auth-center")  //指明要去调用注册中心的哪个微服务的接口
public interface UserFeignService {


}
