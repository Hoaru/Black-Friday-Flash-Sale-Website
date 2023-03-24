package com.pro.snap.vo;

import com.pro.snap.pojo.Users;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetailVo {
    private Users users;
    private GoodsVo goodsVo;
    private int snapStatus;
    private int remainSeconds;
}
