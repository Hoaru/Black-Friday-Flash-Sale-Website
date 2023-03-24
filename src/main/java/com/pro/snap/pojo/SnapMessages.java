package com.pro.snap.pojo;

//秒杀信息

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SnapMessages {

    private Users users;
    private Long goodsId;
}
