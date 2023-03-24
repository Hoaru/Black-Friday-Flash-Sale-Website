package com.pro.snap.config;

//RabbitMQ Fanout配置类
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQFanoutConfig {

//    private static final String QUEUE01 = "queue_fanout01";
//    private static final String QUEUE02 = "queue_fanout02";
//    private static final String EXCHANGE = "fanoutExchange";
//
//    @Bean
//    public Queue queue_fanout(){
//        return new Queue("queue_fanout", true);
//    }
//
//    @Bean
//    public Queue queue_fanout01(){
//        return new Queue(QUEUE01);
//    }
//
//    @Bean
//    public Queue queue_fanout02(){
//        return new Queue(QUEUE02);
//    }
//
//    @Bean
//    public FanoutExchange fanoutExchange(){
//        return new FanoutExchange(EXCHANGE);
//    }
//
//    @Bean
//    public Binding binding01(){
//        return BindingBuilder.bind(queue_fanout01()).to(fanoutExchange());
//    }
//
//    @Bean
//    public Binding binding02(){
//        return BindingBuilder.bind(queue_fanout02()).to(fanoutExchange());
//    }

}
