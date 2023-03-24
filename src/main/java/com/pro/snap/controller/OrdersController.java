package com.pro.snap.controller;

import com.pro.snap.pojo.Users;
import com.pro.snap.service.IOrdersService;
import com.pro.snap.service.impl.OrdersServiceImpl;
import com.pro.snap.vo.OrdersDetailVo;
import com.pro.snap.vo.RespBean;
import com.pro.snap.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/orders")
public class OrdersController {

    @Autowired
    private IOrdersService ordersService;

    // 订单详情
    @RequestMapping("/detail")
    @ResponseBody
    public RespBean detail(Users users, Long ordersId){
        if(users == null){
            return RespBean.error(RespBeanEnum.USER_NOT_EXIST);
        }
        OrdersDetailVo detail = ordersService.detail(ordersId);
        return RespBean.success(detail);
    }

}
