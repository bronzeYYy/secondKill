package cn.chen.second.service.impl;

import cn.chen.second.dao.GoodsDao;
import cn.chen.second.dao.JedisDao;
import cn.chen.second.dao.impl.GoodsDaoImpl;
import cn.chen.second.dao.impl.JedisDaoImpl;
import cn.chen.second.model.Goods;
import cn.chen.second.model.KilledGoods;
import cn.chen.second.service.GoodsService;
import cn.chen.second.util.BeanUtils;
import cn.chen.second.util.DBUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Semaphore;

public class GoodsServiceImpl implements GoodsService {
    private GoodsDao goodsDao = GoodsDaoImpl.getInstance();
    private JedisDao jedisDao = JedisDaoImpl.getInstance();
    private Semaphore semaphore = new Semaphore(10);
    private GoodsServiceImpl() {

    }
    public static GoodsServiceImpl getInstance() {
        return BeanUtils.getOrCreateInstance(GoodsServiceImpl.class);
    }

    @Override
    public List<Goods> getAllGoods() {
        return jedisDao.getAllGoods();
    }

    /*
     * create by: chen
     * description: 秒杀商品，返回结果
     * create time: 12:12 2019-08-14
     * @param goodsId: 商品id
     * @param phone: 秒杀者电话
     * @return boolean: 秒杀结果
     */
    @Override
    public boolean killGoods(int goodsId, String phone) {
        // 从redis获取商品数量，数量存在且库存大于0才继续做

        // 所有的线程一起判断并通过，从此处进行限制线程的接下来访问
        // 限制同时判断redis库存的线程数量
        // 方案1：使用信号量限制共享此处资源（访问缓存中的库存）的线程数。结果：仍然会有大量的线程经过，从而访问数据库。
        // 原因：访问缓存的速度非常快，后面的线程已经访问完了缓存，之前的线程对数据库的操作还没完成
        // 更改为：同时限制缓存和数据库

        /*Goods goods = jedisDao.getGoodsById(goodsId);
        if (goods == null) {
            // 商品不存在
//            throw new IllegalStateException("商品信息不存在");
            System.out.println("商品信息不存在");
            return false;
        }
        if (goods.getNumber() <= 0) {
//            throw new IllegalStateException("商品已被抢完");
            System.out.println("商品已被抢完");
            return false;
        }*/

        // 很多线程都能到达此处
        // 下面进行数据库更新时会有很多线程一起访问，数据库压力仍然很大

        // 过多线程同时获取数据库连接，会出现因为数据库连接过多出现获取连接失败的异常

        try {
            // 信号量设置为10，结果却有12个线程进入
            // 原因：并非12个线程，而是之前的线程已经执行完毕，释放了锁，之后的线程获取到锁才进行操作
            semaphore.acquire();
            Goods goods = jedisDao.getGoodsById(goodsId);
            if (goods == null) {
                // 商品不存在

//            throw new IllegalStateException("商品信息不存在");
                System.out.println("商品信息不存在");
                return false;
            }
            if (goods.getNumber() <= 0) {
//            throw new IllegalStateException("商品已被抢完");
//                System.out.println("商品已被抢完");
                return false;
            }
            Connection connection = DBUtils.getConnection();
            if (connection == null) {
                System.out.println("can not get a connection");
                return false;
            }
            try {
                connection.setAutoCommit(false);
                // 减数量
                System.out.println("获取到数据库连接");
                if (goodsDao.subNumber(connection, goodsId) != 1) {
                    // 很多线程都能到达此处
                    // 在减数量中的sql语句中判断了数量
                    System.out.println("减数量失败");
                    // 测试时12个线程进入，7个线程减数量失败，商品数量为5
                    // 实际为：10个线程进入，2个线程执行完毕之后，其它2个线程获取到锁继续执行
                    return false;
                };
                System.out.println(Thread.currentThread().getName() + ": 减数量成功");
                // 插入成功的信息
                if (goodsDao.addKilledGoods(connection, new KilledGoods(goodsId, phone)) != 1) {
                    System.out.println("插入成功信息");
                    connection.rollback();
                    return false;
                };
                // 更新缓存成功再提交
                // 许多线程都能同时到达此处，redis中减数量的操作必须为原子操作才能保证数量的正确性
                if (!jedisDao.subNumber(goodsId)) {
                    System.out.println("更新缓存");
                    connection.rollback();
                    return false;
                }
                connection.commit();
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            } finally {
                DBUtils.closeConnection(connection);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        } finally {
            System.out.println(Thread.currentThread().getName() + ": 释放锁");
            semaphore.release();
        }

        return false;
    }
}
