package com.pro.snap.config;

//RabbitMQ Direct配置类
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQDirectConfig {

//    private static final String QUEUE01 = "queue_direct01";
//    private static final String QUEUE02 = "queue_direct02";
//    private static final String EXCHANGE = "directExchange";
//    private static final String ROUTINGKEY01 = "queue.red";
//    private static final String ROUTINGKEY02 = "queue.green";
//
//    @Bean
//    public Queue queue_direct01(){
//        return new Queue(QUEUE01);
//    }
//
//    @Bean
//    public Queue queue_direct02(){
//        return new Queue(QUEUE02);
//    }
//
//    @Bean
//    public DirectExchange directExchange(){
//        return new DirectExchange(EXCHANGE);
//    }
//
//    @Bean
//    public Binding binding_direct01(){
//        return BindingBuilder.bind(queue_direct01()).to(directExchange()).with(ROUTINGKEY01);
//    }
//
//    @Bean
//    public Binding binding_direct02(){
//        return BindingBuilder.bind(queue_direct02()).to(directExchange()).with(ROUTINGKEY02);
//    }
}
