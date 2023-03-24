package com.pro.snap.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pro.snap.pojo.Orders;
import com.pro.snap.pojo.Users;
import com.pro.snap.vo.GoodsVo;
import com.pro.snap.vo.OrdersDetailVo;

public interface IOrdersService extends IService<Orders> {

    // 秒杀
    Orders snap(Users users, GoodsVo goods);

    // 订单详情
    OrdersDetailVo detail(Long ordersId);

    // 获取秒杀地址
    String createSnapPath(Users users, Long goodsId);

    // 校验秒杀地址
    Boolean checkSnapPath(Users users, Long goodsId, String path);

    // 校验验证码
    Boolean checkCaptcha(Users users, Long goodsId, String captcha);
}
