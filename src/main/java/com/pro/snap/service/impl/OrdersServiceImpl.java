package com.pro.snap.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pro.snap.exception.GlobalException;
import com.pro.snap.mapper.OrdersMapper;
import com.pro.snap.pojo.*;
import com.pro.snap.service.IGoodsService;
import com.pro.snap.service.IOrdersService;
import com.pro.snap.service.ISnapGoodsService;
import com.pro.snap.service.ISnapOrdersService;
import com.pro.snap.utils.MD5util;
import com.pro.snap.utils.UUIDUtil;
import com.pro.snap.vo.GoodsVo;
import com.pro.snap.vo.OrdersDetailVo;
import com.pro.snap.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements IOrdersService {

    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private ISnapGoodsService snapGoodsService;
    @Autowired
    private ISnapOrdersService snapOrdersService;
    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    //秒杀
    @Transactional
    @Override
    public Orders snap(Users users, GoodsVo goods) {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //获取秒杀商品表并减库存
        SnapGoods snapGoods = snapGoodsService.getOne(new QueryWrapper<SnapGoods>().
                eq("goods_id", goods.getId()));
        snapGoods.setStockCount(snapGoods.getStockCount() - 1);
//        boolean snapGoodsResult = snapGoodsService.update(new UpdateWrapper<SnapGoods>().set("stock_count", snapGoods.getStockCount())
//                .eq("id", snapGoods.getId()).gt("stock_count", 0));
//        if(!snapGoodsResult){
//            return null;
//        }
//        boolean result = snapGoodsService.update(new UpdateWrapper<SnapGoods>()
//                .setSql("stock_count = stock_count-1")
//                .eq("goods_id", goods.getId())
//                .gt("stock_count", 0));
//        if(!result){
//            return null;
//        }
        snapGoodsService.update(new UpdateWrapper<SnapGoods>()
                .setSql("stock_count = stock_count-1")
                .eq("goods_id", goods.getId())
                .gt("stock_count", 0));
        if(snapGoods.getStockCount() < 1){
            // 判断是否还有库存
            valueOperations.set("isStockEmpty:" + goods.getId(), "0");
            return null;
        }
        //生成订单
        Orders orders = new Orders();
        orders.setUsersId(users.getId());
        orders.setGoodsId(goods.getId());
        orders.setDeliveryAddrId(0L);
        orders.setGoodsName(goods.getGoodsName());
        orders.setGoodsCount(1);
        orders.setGoodsPrice(snapGoods.getSnapPrice());
        orders.setOrdersChannel(1);
        orders.setStatus(0);
        orders.setCreateDate(new Date());
        ordersMapper.insert(orders);
        //生成秒杀订单
        SnapOrders snapOrders = new SnapOrders();
        snapOrders.setUsersId(users.getId());
        snapOrders.setOrdersId(orders.getId());
        snapOrders.setGoodsId(goods.getId());
        snapOrdersService.save(snapOrders);
        redisTemplate.opsForValue().set("orders:"+users.getId()+":"+goods.getId(), snapOrders);

        return orders;
    }

    // 订单详情
    @Override
    public OrdersDetailVo detail(Long ordersId) {
        if(ordersId == null){
            throw new GlobalException(RespBeanEnum.ORDER_NOT_EXIST);
        }
        Orders orders = ordersMapper.selectById(ordersId);
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(orders.getGoodsId());
        OrdersDetailVo detail = new OrdersDetailVo();
        detail.setOrders(orders);
        detail.setGoodsVo(goodsVo);
        return detail;
    }

    // 获取秒杀地址
    @Override
    public String createSnapPath(Users users, Long goodsId) {
        //随机生成地址，“123456”加盐
        String str = MD5util.md5(UUIDUtil.uuid() + "123456");
        redisTemplate.opsForValue().set("snapPath:" + users.getId() + ":" + goodsId, str, 60, TimeUnit.SECONDS);
        return str;
    }

    // 校验秒杀地址
    @Override
    public Boolean checkSnapPath(Users users, Long goodsId, String path) {
        if(users == null || goodsId < 0 || StringUtils.isEmpty(path)) {
            return false;
        }
        String redisPath = (String) redisTemplate.opsForValue().get("snapPath:" + users.getId() + ":" + goodsId);
        //System.out.println("redisPath: " + redisPath);
        return path.equals(redisPath);
    }

    @Override
    public Boolean checkCaptcha(Users users, Long goodsId, String captcha) {
        if(users == null || goodsId < 0 || StringUtils.isEmpty(captcha)) {
            return false;
        }
        String redisCaptcha = (String) redisTemplate.opsForValue().get("captcha:" + users.getId() + ":" + goodsId);
        //System.out.println("redisCaptcha: " + redisCaptcha);
        return captcha.equals(redisCaptcha);
    }
}
