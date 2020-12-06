package com.hedon.rabbitmq;

import common.code.ResultCode;
import common.dto.TaskNotificationDto;
import common.entity.VerificationCode;
import common.vo.common.ResponseBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.AbstractJavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * 验证码消费者
 *
 * @author Hedon Wang
 * @create 2020-11-30 10:54
 */
@Component
@Slf4j
public class VerificationCodePublisher {

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
     * 发送注册验证码
     *
     * @param verificationCode
     * @return
     */
    public ResponseBean sendCode(VerificationCode verificationCode) {
        if (verificationCode != null) {
            try {
                //设置消息传输格式为JSON
                rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
                //绑定路由
                rabbitTemplate.setExchange(environment.getProperty("mq.producer.basic.exchange.name"));
                //绑定路由
                rabbitTemplate.setRoutingKey(environment.getProperty("mq.producer.basic.routing.key.name"));
                //发送消息
                rabbitTemplate.convertAndSend(verificationCode, new MessagePostProcessor() {
                    @Override
                    public Message postProcessMessage(Message message) throws AmqpException {
                        MessageProperties messageProperties = message.getMessageProperties();
                        messageProperties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                        messageProperties.setHeader(AbstractJavaTypeMapper.DEFAULT_CONTENT_CLASSID_FIELD_NAME, TaskNotificationDto.class);
                        //设置延迟时间 —— 5分钟内有
                        messageProperties.setExpiration(String.valueOf(5 * 60 * 1000));
                        return message;
                    }
                });
                //发送成功
                log.info("发送验证码成功，内容为：({})", verificationCode);
                return ResponseBean.success();
            } catch (Exception e) {
                log.error("发送验证码失败，内容为：({})，失败原因为：({})", verificationCode, e.getMessage());
                return ResponseBean.fail(ResultCode.REGISTER_FAILED);
            }
        } else {
            log.error("发送验证码失败，原因为：没有传递任务信息过来");
            return ResponseBean.fail(ResultCode.REGISTER_FAILED);
        }
    }
}
