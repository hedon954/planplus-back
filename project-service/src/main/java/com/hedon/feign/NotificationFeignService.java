package com.hedon.feign;

import common.dto.TaskNotificationDto;
import common.entity.VerificationCode;
import common.vo.common.ResponseBean;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 远程调用 notification-service 的接口
 *
 * @author Hedon Wang
 * @create 2020-11-09 10:49
 */
@Component
@FeignClient(value = "notification-service")
public interface NotificationFeignService {

    /**
     * 发送通知信息到死信队列中，等待消费者消费
     * @param dto  信息体
     * @return
     */
    @PostMapping("/notification/notify")
    ResponseBean sendNotificationMsg(@RequestBody TaskNotificationDto dto);


    /**
     * 发送注册码到死信队列中，等待消费者消费
     * @param verificationCode
     * @return
     */
    @PostMapping("/notification/code/register")
    ResponseBean sendRegisterCode(@RequestBody VerificationCode verificationCode);

}
