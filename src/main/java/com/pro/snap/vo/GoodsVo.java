package com.pro.snap.vo;

import com.pro.snap.pojo.Goods;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoodsVo {
    private Long id;

    private String goodsName;

    private String goodsTitle;

    private String goodsImg;

    private String goodsDetail;

    private BigDecimal goodsPrice;

    // -1 refers to unlimit
    private Integer goodsStock;

    private BigDecimal snapPrice;

    private Integer stockCount;

    private Date startDate;

    private Date endDate;
}
