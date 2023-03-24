package com.pro.snap.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//消息发送者
@Service
@Slf4j
public class MQSender {
    @Autowired
    private RabbitTemplate rabbitTemplate;

//    public void send(Object msg){
//        log.info("Send message: " + msg);
//        //key为空表示广播，否则为direct
//        rabbitTemplate.convertAndSend("Exchange", "", msg);
//    }
//
//    public void send_fanout(Object msg){
//        log.info("Send message: " + msg);
//        rabbitTemplate.convertAndSend("fanoutExchange", "", msg);
//    }
//    public void send_direct01(Object msg){
//        log.info("Send message from direct01 red: " + msg);
//        rabbitTemplate.convertAndSend("directExchange", "queue.red", msg);
//    }
//    public void send_direct02(Object msg){
//        log.info("Send message from direct02 green: " + msg);
//        rabbitTemplate.convertAndSend("directExchange", "queue.green", msg);
//    }
//    public void send_topic01(Object msg){
//        log.info("Send message from topic01 (Queue01 received): " + msg);
//        rabbitTemplate.convertAndSend("topicExchange", "queue.blue.mesage", msg);
//    }
//    public void send_topic02(Object msg){
//        log.info("Send message from topic02 (Queue01&02 received): " + msg);
//        rabbitTemplate.convertAndSend("topicExchange", "message.queue.yellow.message", msg);
//    }
//    public void send_headers01(String msg){
//        log.info("Send message from headers01 (Queue01&02 received): " + msg);
//        MessageProperties properties = new MessageProperties();
//        properties.setHeader("color", "black");
//        properties.setHeader("speed", "low");
//        Message message = new Message(msg.getBytes(), properties);
//        rabbitTemplate.convertAndSend("headersExchange", "", message);
//    }
//    public void send_headers02(String msg){
//        log.info("Send message from headers02 (Queue01 received): " + msg);
//        MessageProperties properties = new MessageProperties();
//        properties.setHeader("clor", "black");
//        properties.setHeader("speed", "low");
//        Message message = new Message(msg.getBytes(), properties);
//        rabbitTemplate.convertAndSend("headersExchange", "", message);
//    }

    // 发送秒杀信息
    public void sendSnapMessages_topic(String msg){
        log.info("Send message (Topic Mode): " + msg);
        rabbitTemplate.convertAndSend("snapExchange_topic", "snap.message", msg);
    }
}
