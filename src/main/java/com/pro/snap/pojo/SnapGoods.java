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
@TableName("t_snap_goods")
public class SnapGoods implements Serializable {

    private static final long serialVersionUID = 1L;

    //秒杀商品id
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    //商品id
    private Long goodsId;

    private BigDecimal snapPrice;

    private Integer stockCount;

    private Date startDate;

    private Date endDate;

}
