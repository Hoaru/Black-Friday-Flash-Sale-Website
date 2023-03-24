package com.pro.snap.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_snap_orders")
public class SnapOrders implements Serializable {

    private static final long serialVersionUID = 1L;

    //秒杀订单id
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    //用户id
    private Long usersId;

    //订单id
    private Long ordersId;

    //商品id
    private Long goodsId;

}
