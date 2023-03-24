package com.pro.snap.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pro.snap.mapper.SnapOrdersMapper;
import com.pro.snap.pojo.SnapOrders;
import com.pro.snap.pojo.Users;
import com.pro.snap.service.ISnapOrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class SnapOrdersServiceImpl extends ServiceImpl<SnapOrdersMapper, SnapOrders> implements ISnapOrdersService {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private SnapOrdersMapper snapOrdersMapper;

    // 取秒杀结果，返回值 -> ordersId：成功， -1：失败， 0：排队中
    @Override
    public Long getResult(Users users, Long goodsId) {
        SnapOrders snapOrders = snapOrdersMapper.selectOne(new QueryWrapper<SnapOrders>()
                .eq("users_id", users.getId())
                .eq("goods_id", goodsId));
        if(snapOrders != null){
            return snapOrders.getOrdersId();
        }
        else if(redisTemplate.hasKey("isStockEmpty:" + goodsId)){
            return -1L;
        }
        return 0L;
    }
}
