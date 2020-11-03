package com.hedon.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 的配置类
 *
 * @author Hedon Wang
 * @create 2020-11-03 19:28
 */
@Configuration
@Slf4j
public class RabbitMqConfig {

    //注入 RabbitMQ 的连接工厂实例
    @Autowired
    private CachingConnectionFactory connectionFactory;

    //注入消息监听器所在的容器工厂配置类实例
    @Autowired
    private SimpleRabbitListenerContainerFactoryConfigurer factoryConfigurer;


    /**
     * 单一消费者实例的配置
     */
    @Bean(name = "singleListenerContainerFactory")
    public SimpleRabbitListenerContainerFactory singleListenerContainerFactory(){
        //申明消息监听器所在的容器工厂
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        //设置消息在传输中的格式为 JSON
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        //设置并发消费者实例的初始数量 => 1 个
        factory.setConcurrentConsumers(1);
        //设置并发消费者实例的最大数量 => 1 个
        factory.setMaxConcurrentConsumers(1);
        //设置并发消费者实例中每个实例拉取的消息数量 => 1个
        factory.setPrefetchCount(1);
        //返回实例
        return factory;
    }


    /**
     * 多个消费者实例的配置，主要是针对高并发业务场景的配置
     */
    @Bean(name = "multiListenerContainerFactory")
    public SimpleRabbitListenerContainerFactory multiListenerContainerFactory(){
        //申明消息监听器所在的容器工厂
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        //设置消息传输格式为 JSON
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        //设置消息的确认消费模式，这里为手动确认 MANUAL
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        //设置并发消费者实例的初始数量 => 10 个
        factory.setConcurrentConsumers(10);
        //设置并发消费者实例的最大数量 => 20 个
        factory.setMaxConcurrentConsumers(20);
        //设置并发消费者实例中每个实例拉取的消息数量，在这里为 100 个
        factory.setPrefetchCount(100);
        //返回实例
        return factory;
    }


}
