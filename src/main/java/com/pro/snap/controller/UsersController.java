package com.pro.snap.controller;

import com.pro.snap.pojo.Users;
import com.pro.snap.rabbitmq.MQSender;
import com.pro.snap.vo.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

//前端控制器
@Controller
@RequestMapping("/users")
public class UsersController {

    @Autowired
    private MQSender mqSender;

    //用户信息（测试）
    @RequestMapping("/info")
    @ResponseBody
    public RespBean info(Users users){
        return RespBean.success(users);
    }

//    //测试发送RabbitMQ消息
//    @RequestMapping("/mq")
//    @ResponseBody
//    public void mq(){
//        mqSender.send("Hello");
//    }
//
//    //测试发送RabbitMQ消息: Fanout模式
//    @RequestMapping("/mq/fanout")
//    @ResponseBody
//    public void mq_fanout(){
//        mqSender.send_fanout("Hello fanout");
//    }
//
//    //测试发送RabbitMQ消息: Direct模式
//    @RequestMapping("/mq/direct01")
//    @ResponseBody
//    public void mq_direct01(){
//        mqSender.send_direct01("Hello direct01, red");
//    }
//
//    @RequestMapping("/mq/direct02")
//    @ResponseBody
//    public void mq_direct02(){
//        mqSender.send_direct02("Hello direct02, green");
//    }
//
//    //测试发送RabbitMQ消息: Topic模式
//    @RequestMapping("/mq/topic01")
//    @ResponseBody
//    public void mq_topic01(){
//        mqSender.send_topic01("Hello topic01, queue.blue.message");
//    }
//
//    @RequestMapping("/mq/topic02")
//    @ResponseBody
//    public void mq_topic02(){
//        mqSender.send_topic02("Hello topic02, message.queue.yellow.message");
//    }
//
//    //测试发送RabbitMQ消息: Headers模式
//    @RequestMapping("/mq/headers01")
//    @ResponseBody
//    public void mq_headers01(){
//        mqSender.send_headers01("Hello headers01");
//    }
//
//    @RequestMapping("/mq/headers02")
//    @ResponseBody
//    public void mq_headers02(){
//        mqSender.send_headers02("Hello headers02");
//    }

}
