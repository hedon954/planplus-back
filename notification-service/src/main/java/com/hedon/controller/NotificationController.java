package com.hedon.controller;

import com.hedon.rabbitmq.VerificationCodePublisher;
import common.entity.VerificationCode;
import common.exception.ServiceException;
import common.vo.common.ResponseBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 通知的控制器，用于暴露接口给 project-service 进行调用
 *
 * @author Hedon Wang
 * @create 2020-11-09 10:44
 */
@RestController
@RequestMapping("/notification")
@Slf4j
public class NotificationController {


    @Autowired
    private VerificationCodePublisher verificationCodePublisher;


    @PostMapping("/code")
    public ResponseBean sendCode(@RequestBody VerificationCode verificationCode){
        log.info("正在调用通知模块的接口 sendCode，verificationCode：" + verificationCode);
        try {
            return verificationCodePublisher.sendCode(verificationCode);
        }catch (ServiceException e){
            return e.getFailResponse();
        }
    }



}
