package cn.chen.second.dao;

import cn.chen.second.model.Goods;

import java.util.List;

public interface JedisDao {
    List<Goods> getAllGoods();
    Goods getGoodsById(int goodsId);

    boolean subNumber(int goodsId);
}
