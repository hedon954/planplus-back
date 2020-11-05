package com.hedon.rabbitmq;

import com.hedon.message.DeadInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

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


    /**
     * 监听真正的队列-消费队列中的消息-面向消费者
     *
     * @param deadInfo  消息
     */
    @RabbitListener(queues = "${mq.consumer.real.queue.name}",containerFactory = "multiListenerContainerFactory")
    public void consumeDeadMsg(@Payload DeadInfo deadInfo){
        if (deadInfo != null){
            try {
                log.info("死信队列实战-监听真正的队列-消费队列中的信息：{}，时间：{}",deadInfo, LocalDateTime.now());
            }catch (Exception e){
                log.error("死信队列实战-监听真正的队列-消费队列中的信息发生异常：{}",deadInfo,e.fillInStackTrace());
            }
        }
    }
}
