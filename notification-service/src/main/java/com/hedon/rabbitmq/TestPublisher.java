package com.hedon.rabbitmq;

import com.hedon.message.DeadInfo;
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

import java.time.LocalDateTime;

/**
 * RabbitMQ 测试生产者
 *
 * @author Hedon Wang
 * @create 2020-11-05 23:37
 */
@Component
@Slf4j
public class TestPublisher {

    //读取环境变量
    @Autowired
    private Environment environment;

    //注入 RabbitMQ 操作组件
    @Autowired
    private RabbitTemplate rabbitTemplate;


    /**
     * 发送消息
     *
     * @param deadInfo 消息
     */
    public void sendDeadMsg(DeadInfo deadInfo){
        if (deadInfo != null){
            try {
                //设置消息传输格式 JSON
                rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
                //绑定交换机
                rabbitTemplate.setExchange(environment.getProperty("mq.producer.basic.exchange.name"));
                //绑定路由
                rabbitTemplate.setRoutingKey(environment.getProperty("mq.producer.basic.routing.key.name"));
                //发送消息
                rabbitTemplate.convertAndSend(deadInfo, new MessagePostProcessor() {
                    @Override
                    public Message postProcessMessage(Message message) throws AmqpException {
                        //获取消息属性
                        MessageProperties messageProperties = message.getMessageProperties();
                        //设置消息持久化
                        messageProperties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                        //设置消息类型
                        messageProperties.setHeader(AbstractJavaTypeMapper.DEFAULT_CONTENT_CLASSID_FIELD_NAME,DeadInfo.class);
                        //设置消息 TTL：当消息和队列通知都设置了 TTL 时，则取较短时间的值，单位为 ms
                        messageProperties.setExpiration(String.valueOf(10000));
                        return message;
                    }
                });
                //打印日志
                log.info("死信队列实战-发送对象类型的信息入死信队列-生产者-内容为：{}，时间：{}",deadInfo, LocalDateTime.now());
            }catch (Exception e){
                log.error("死信队列实战-发送对象类型的信息入死信队列-生产者-发生异常：{}",deadInfo,e.fillInStackTrace());
            }
        }
    }


}
