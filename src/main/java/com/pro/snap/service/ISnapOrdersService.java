package com.pro.snap.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pro.snap.pojo.SnapOrders;
import com.pro.snap.pojo.Users;

public interface ISnapOrdersService extends IService<SnapOrders> {

    // 获取秒杀结果，返回值 -> ordersId：成功， -1：失败， 0：排队中
    Long getResult(Users users, Long goodsId);
}
