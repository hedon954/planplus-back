package com.hedon.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * RabbitMQ 测试消费者
 *
 * @author Hedon Wang
 * @create 2020-11-05 23:48
 */
@Component
@Slf4j
public class TestConsumer {

    //注入 RabbitMQ 操作组件
    @Autowired
    private RabbitTemplate rabbitTemplate;




}
