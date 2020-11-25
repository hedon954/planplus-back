package com.hedon.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.HashMap;
import java.util.Map;

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

    //注入环境变量
    @Autowired
    private Environment environment;


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
        factoryConfigurer.configure(factory,connectionFactory);
        //设置消息传输格式为 JSON
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        //设置消息的确认消费模式，这里先设置为自动确认 AUTO，后面看效果再看要不要改成手动确认 MANUAL
        factory.setAcknowledgeMode(AcknowledgeMode.AUTO);
        //设置并发消费者实例的初始数量 => 10 个
        factory.setConcurrentConsumers(10);
        //设置并发消费者实例的最大数量 => 20 个
        factory.setMaxConcurrentConsumers(20);
        //设置并发消费者实例中每个实例拉取的消息数量，在这里为 5 个
        factory.setPrefetchCount(1);
        //返回实例
        return factory;
    }

    /**
     * 配置 RabbitMQ 操作组件
     */
    @Bean
    public RabbitTemplate rabbitTemplate(){
        //设置"发送消息后进行确认"
        connectionFactory.setPublisherConfirms(true);
        //设置"发送消息后返回确认信息"
        connectionFactory.setPublisherReturns(true);
        //构造发送消息组件实例对象
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMandatory(true);
        //发送消息后，如果发送成功，则输出"消息发送成功"的反馈信息
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                log.info("消息发送成功：correlationData({}),ack({}),case({})",correlationData,ack,cause);
            }
        });
        //发送消息后，如果发送失败，则输出"消息发送失败-消息丢失"的反馈信息
        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
                log.info("消息丢失：exchange({}),route({}),replyCode({}),replyText({}),message:{}",exchange,routingKey,replyCode,replyText,message);
            }
        });
        return rabbitTemplate;
    }


    /**
     * =============================
     *          死信队列配置
     * =============================
     */

    /**
     * 创建死信队列
     */
    @Bean(name = "basicDeadQueue")
    public Queue basicDeadQueue(){
        //创建死信队列的组成部分 map，用于存放组成部分的相关成员
        Map<String,Object> args = new HashMap<>();
        //创建死信交换机
        args.put("x-dead-letter-exchange",environment.getProperty("mq.dead.exchange.name"));
        //创建死信路由
        args.put("x-dead-letter-routing-key",environment.getProperty("mq.dead.routing.key.name"));
        //设定 TTL，在消息那边设定，单位是 ms，这里设置大一些，最大是100天
        args.put("x-message-ttl",100*24*60*60*100);
        //创建并返回死信队列实例
        return new Queue(environment.getProperty("mq.dead.queue.name"),true,false,false,args);
    }

    /**
     * 创建"基本消息模型"的基本交换机 —— 面向生产者
     */
    @Bean(name = "basicProducerExchange")
    public TopicExchange basicProducerExchange(){
        return new TopicExchange(environment.getProperty("mq.producer.basic.exchange.name"),true,false);
    }

    /**
     * 创建"基本消息模型"的基本绑定-基本交换机+基本路由 - 面向生产者
     */
    @Bean(name = "basicProducerBinding")
    public Binding basicProducerBinding(){
        return BindingBuilder.bind(basicDeadQueue())
                .to(basicProducerExchange())
                .with(environment.getProperty("mq.producer.basic.routing.key.name"));
    }

    /**
     * 创建真正的队列 —— 面向消费者
     */
    @Bean(name = "realConsumerQueue")
    public Queue realConsumerQueue(){
        return new Queue(environment.getProperty("mq.consumer.real.queue.name"),true);
    }

    /**
     * 创建死信交换机
     */
    @Bean(name = "basicDeadExchange")
    public TopicExchange basicDeadExchange(){
        return new TopicExchange(environment.getProperty("mq.dead.exchange.name"),true,false);
    }

    /**
     * 创建死信路由及其绑定 - 死信交换机 - 真正队列
     */
    @Bean(name = "basicDeadBinding")
    public Binding basicDeadBinding(){
        return BindingBuilder.bind(realConsumerQueue())
                .to(basicDeadExchange())
                .with(environment.getProperty("mq.dead.routing.key.name"));
    }

}
