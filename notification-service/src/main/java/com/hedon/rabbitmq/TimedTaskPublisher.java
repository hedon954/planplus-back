package com.hedon.rabbitmq;

import com.hedon.service.IBaiduInfoService;
import common.code.ResultCode;
import common.dto.TaskNotificationDto;
import common.entity.BaiduInfo;
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
 * 定时任务生产者
 *
 * @author Hedon Wang
 * @create 2020-11-06 10:37
 */
@Component
@Slf4j
public class TimedTaskPublisher {

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
     * 百度信息 Service
     */
    @Autowired
    private IBaiduInfoService baiduInfoService;

    /**
     * 发送信息 —— 生产者
     *
     * @param dto 信息封装类
     */
    public ResponseBean sendTimedTaskMsg(TaskNotificationDto dto){
        if (dto != null){
            try {
                //设置消息传输格式为JSON
                rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
                //绑定路由
                rabbitTemplate.setExchange(environment.getProperty("mq.producer.basic.exchange.name"));
                //绑定路由
                rabbitTemplate.setRoutingKey(environment.getProperty("mq.producer.basic.routing.key.name"));
                //发送消息
                rabbitTemplate.convertAndSend(dto, new MessagePostProcessor() {
                    @Override
                    public Message postProcessMessage(Message message) throws AmqpException {
                        MessageProperties messageProperties = message.getMessageProperties();
                        messageProperties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                        messageProperties.setHeader(AbstractJavaTypeMapper.DEFAULT_CONTENT_CLASSID_FIELD_NAME,TaskNotificationDto.class);
                        return message;
                    }
                });
                //发送成功
                log.info("创建任务成功，任务内容为：({})",dto);
                return ResponseBean.success();
            }catch (Exception e){
                log.error("定时任务创建失败，任务内容为：({})，失败原因为：({})",dto,e.getMessage());
                return ResponseBean.fail(ResultCode.TIMED_TASK_CREATE_FAILED);
            }
        }else{
            log.error("定时任务创建失败，失败原因为：没有传递任务信息过来");
            return ResponseBean.fail(ResultCode.TIMED_TASK_CREATE_FAILED);
        }
    }

}
