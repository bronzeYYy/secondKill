package cn.chen.second.dao.impl;

import cn.chen.second.model.KilledGoods;
import cn.chen.second.service.impl.GoodsServiceImpl;
import cn.chen.second.util.BeanUtils;
import cn.chen.second.util.DBResultUtils;
import cn.chen.second.util.DBUtils;
import cn.chen.second.dao.GoodsDao;
import cn.chen.second.model.Goods;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class GoodsDaoImpl implements GoodsDao {
    private GoodsDaoImpl() {}
    public static GoodsDao getInstance() {
        return BeanUtils.getOrCreateInstance(GoodsDaoImpl.class);
    }
    @Override
    public List<Goods> getAllGoods() {
        Connection connection = DBUtils.getConnection();
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery("select * from goods");
            return DBResultUtils.getResultObjectList(resultSet, Goods.class);
        } catch (SQLException | NullPointerException e) {
            e.printStackTrace();
        } finally {
            DBUtils.close(connection, statement, resultSet);
        }
        return new LinkedList<>();
    }

    @Override
    public int subNumber(Connection connection, int goodId) throws SQLException {
        Statement statement = connection.createStatement();
        int i = statement.executeUpdate("update goods set number = number - 1 where goods_id = " + goodId + " and number > 0");
        DBUtils.closeStatement(statement);
        return i;
    }

    @Override
    public int addKilledGoods(Connection connection, KilledGoods killedGoods) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("insert into killed_goods value (?, ?, now())");
        preparedStatement.setInt(1, killedGoods.getGoodsId());
        preparedStatement.setString(2, killedGoods.getPhone());
        int i = preparedStatement.executeUpdate();
        DBUtils.closeStatement(preparedStatement);
        return i;
    }
}
