package com.pro.snap.rabbitmq;

//消息发送者

import com.pro.snap.pojo.SnapMessages;
import com.pro.snap.pojo.SnapOrders;
import com.pro.snap.pojo.Users;
import com.pro.snap.service.IGoodsService;
import com.pro.snap.service.IOrdersService;
import com.pro.snap.utils.JsonUtil;
import com.pro.snap.vo.GoodsVo;
import com.pro.snap.vo.RespBean;
import com.pro.snap.vo.RespBeanEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MQReceiver {
//    @RabbitListener(queues = "queue")
//    public void receive(Object obj){ log.info("queue receive message: " + obj); }
//
//    @RabbitListener(queues = "queue_fanout01")
//    public void receive_fanout01(Object obj){ log.info("queue_fanout01 receive message: " + obj); }
//
//    @RabbitListener(queues = "queue_fanout02")
//    public void receive_fanout02(Object obj){ log.info("queue_fanout02 receive message: " + obj); }
//
//    @RabbitListener(queues = "queue_direct01")
//    public void receive_direct01(Object obj){ log.info("queue_direct01 receive message: " + obj); }
//
//    @RabbitListener(queues = "queue_direct02")
//    public void receive_direct02(Object obj){ log.info("queue_direct02 receive message: " + obj); }
//
//    @RabbitListener(queues = "queue_topic01")
//    public void receive_topic01(Object obj){ log.info("queue_topic01 receive message: " + obj); }
//
//    @RabbitListener(queues = "queue_topic02")
//    public void receive_topic02(Object obj){ log.info("queue_topic02 receive message: " + obj); }
//
//    @RabbitListener(queues = "queue_headers01")
//    public void receive_headers01(Message msg){ log.info("queue_headers01 receive message: " + new String(msg.getBody())); }
//
//    @RabbitListener(queues = "queue_headers02")
//    public void receive_headers02(Message msg){ log.info("queue_headers02 receive message: " + new String(msg.getBody())); }

    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private IOrdersService ordersService;
    @Autowired
    private RedisTemplate redisTemplate;

    //下单操作
    @RabbitListener(queues = "snapQueue_topic")
    public void receive_topic(String msg){
        log.info("snapQueue_topic receive message: " + msg);
        SnapMessages snapMessages = JsonUtil.jsonStr2Object(msg, SnapMessages.class);
        Long goodsId = snapMessages.getGoodsId();
        Users users = snapMessages.getUsers();
        // 判断库存
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        if(goodsVo.getStockCount() < 1){
            return;
        }
        //log.info("成功判断库存");
        // 判断是否重复抢购
        SnapOrders snapOrders = (SnapOrders) redisTemplate.opsForValue()
                .get("orders:" + users.getId()+":" + goodsId);
        if (snapOrders != null){
            return;
        }
        // 下单操作
        ordersService.snap(users, goodsVo);
    }
}
