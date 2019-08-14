package cn.chen.second.service;

import cn.chen.second.model.Goods;

import java.util.List;

public interface GoodsService {
    List<Goods> getAllGoods();
    boolean killGoods(int goodsId, String phone);
}
