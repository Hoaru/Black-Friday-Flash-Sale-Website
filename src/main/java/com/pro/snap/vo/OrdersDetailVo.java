package com.pro.snap.vo;

import com.pro.snap.pojo.Orders;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrdersDetailVo {
    private Orders orders;
    private GoodsVo goodsVo;
}
