package com.hedon.controller;

import com.hedon.rabbitmq.TimedTaskPublisher;
import common.dto.TaskNotificationDto;
import common.exception.ServiceException;
import common.vo.common.ResponseBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 通知的控制器，用于暴露接口给 project-service 进行调用
 *
 * @author Hedon Wang
 * @create 2020-11-09 10:44
 */
@RestController
@RequestMapping("/notification")
public class NotificationController {

    @Autowired
    private TimedTaskPublisher publisher;


    /**
     * 通知接口
     * @param dto
     * @return
     */
    @PostMapping("/notify")
    public ResponseBean sendNotificationMsg(@RequestBody TaskNotificationDto dto){
       System.out.println("正在调用通知模块的接口，dto：" + dto);
       try{
           return publisher.sendTimedTaskMsg(dto);
       }catch (ServiceException e){
           return e.getFailResponse();
       }
    }



}
