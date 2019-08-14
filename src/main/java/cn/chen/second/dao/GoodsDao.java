package cn.chen.second.dao;

import cn.chen.second.model.Goods;
import cn.chen.second.model.KilledGoods;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface GoodsDao {
    List<Goods> getAllGoods();
    int subNumber(Connection connection, int goodId) throws SQLException;
    int addKilledGoods(Connection connection, KilledGoods killedGoods) throws SQLException;
}
