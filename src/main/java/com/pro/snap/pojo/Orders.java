package com.pro.snap.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_orders")
public class Orders implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long usersId;

    private Long goodsId;

    private Long deliveryAddrId;

    //冗余过来的商品名称
    private String goodsName;

    private Integer goodsCount;

    private BigDecimal goodsPrice;

    //1PC 2Android 3IOS
    private Integer ordersChannel;

    //订单状态：1已支付 2已发货 3已收货 4已退款 5已完成
    private Integer status;

    private Date createDate;

    private Date payDate;


}
