package com.hedon.rabbitmq;

import common.code.ResultCode;
import common.dto.TaskNotificationDto;
import common.entity.DidaTask;
import common.entity.VerificationCode;
import common.mapper.VerificationCodeMapper;
import common.vo.common.ResponseBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.AbstractJavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * @author Hedon Wang
 * @create 2020-11-30 10:54
 */
@Component
@Slf4j
public class VerificationCodeComsumer {

    /**
     * 验证码 mapper
     */
    @Autowired
    private VerificationCodeMapper verificationCodeMapper;

    /**
     * 读取环境变量
     */
    @Autowired
    private Environment environment;

    /**
     * 注入 RabbitMQ 操作组件
     */
    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 消费消息 —— 检查验证码是否失效，如果失效，说明5分钟了用户还没有使用验证码，那么手动失效验证码
     *
     * @param verificationCode 消息
     */
    @RabbitListener(queues = "${mq.consumer.real.queue.name}",containerFactory = "multiListenerContainerFactory")
    public void consumeVerificationCodeMsg(@Payload VerificationCode verificationCode){
        if (verificationCode != null){
            try {
                Integer codeId = verificationCode.getCodeId();
                VerificationCode code = verificationCodeMapper.selectById(codeId);
                if (code.getIsActive() == 1){
                    //如果还有效，则手动失效
                    code.setIsActive(0);
                    verificationCodeMapper.updateById(code);
                    //并且删除
                    verificationCodeMapper.deleteById(codeId);
                    log.info("验证码超时使用，失效验证码成功");
                }
                else{
                    verificationCodeMapper.deleteById(codeId);
                    log.info("验证码已被使用，无需手动失效");
                }
            }catch (Exception e){
                log.error("失效验证码过程中出现错误，错误信息为：({})",e.getMessage());
            }
        }
    }

}
