package com.pro.snap.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pro.snap.pojo.Goods;
import com.pro.snap.vo.GoodsVo;

import java.util.List;

public interface GoodsMapper extends BaseMapper<Goods> {


    //获取商品列表
    List<GoodsVo> findGoodsVo();

    //获取商品详情
    GoodsVo findGoodsVoByGoodsId(Long goodsId);

}