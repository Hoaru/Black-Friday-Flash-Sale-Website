package com.pro.snap.config;

//RabbitMQ Headers配置类
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQHeadersConfig {

//    private static final String QUEUE01 = "queue_headers01";
//    private static final String QUEUE02 = "queue_headers02";
//    private static final String EXCHANGE = "headersExchange";
//
//    @Bean
//    public Queue queue_headers01(){ return new Queue(QUEUE01); }
//
//    @Bean
//    public Queue queue_headers02(){
//        return new Queue(QUEUE02);
//    }
//
//    @Bean
//    public HeadersExchange headersExchange(){
//        return new HeadersExchange(EXCHANGE);
//    }
//
//    @Bean
//    public Binding binding_headers01(){
//        Map<String, Object> map = new HashMap<>();
//        map.put("color", "black");
//        map.put("speed", "low");
//        return BindingBuilder.bind(queue_headers01()).to(headersExchange()).whereAny(map).match();
//    }
//
//    @Bean
//    public Binding binding_headers02(){
//        Map<String, Object> map = new HashMap<>();
//        map.put("color", "black");
//        map.put("speed", "low");
//        return BindingBuilder.bind(queue_headers02()).to(headersExchange()).whereAll(map).match();
//    }

}
