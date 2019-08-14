package cn.chen.second.dao.impl;

import cn.chen.second.dao.GoodsDao;
import cn.chen.second.dao.JedisDao;
import cn.chen.second.model.Goods;
import cn.chen.second.util.BeanUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.LinkedList;
import java.util.List;

public class JedisDaoImpl implements JedisDao {
    private JedisPool jedisPool = BeanUtils.getOrCreateJedisPool();
    private final GoodsDao goodsDao = GoodsDaoImpl.getInstance();
    private JedisDaoImpl() {}
    public static JedisDao getInstance() {
        return BeanUtils.getOrCreateInstance(JedisDaoImpl.class);
    }
    @Override
    public List<Goods> getAllGoods() {
        Jedis jedis = jedisPool.getResource();
        if (!jedis.exists("goods")) {
            // 商品不存在。从数据库获取
            // 此时如果许多请求同时到来，都会从数据库中获取，造成缓存击穿
            // 方案1：和单例模式一样，只有一个线程进行读取缓存（创建对象）。耗时：5000线程4s772ms
            synchronized(goodsDao) {
                System.out.println(Thread.currentThread().getName() + ": 进入同步块");
                if (!jedis.exists("goods")) {
                    System.out.println(Thread.currentThread().getName() + ": 从数据库获取");
                    List<Goods> goods = goodsDao.getAllGoods();
                    goods.forEach(goods1 -> jedis.hset("goods", goods1.getGoodsId() + "", JSON.toJSONString(goods1)));
                    jedis.close();
                    return goods;
                }
            }
            // 方案2：采用自旋锁，未拿到锁的线程在锁外面做自选操作。线程越多，设置锁的有效期应该越长。5000线程5s696
            /*while (!jedis.exists("goods")) {
                // 尝试获取锁
                if (getLock(jedis)) {
                    // 获取到锁，开始从数据库获取数据
                    try {
                        System.out.println(Thread.currentThread().getName() + ": 从数据库获取");
                        List<Goods> goods = goodsDao.getAllGoods();
                        goods.forEach(goods1 -> jedis.hset("goods", goods1.getGoodsId() + "", JSON.toJSONString(goods1)));
                        return goods;
                    } finally {
                        // 释放锁
                        releaseLock(jedis);
                        jedis.close();
                    }
                    //
                }
                // 否则，稍作等待继续获取
                try {
                    Thread.sleep(100L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }*/
        }
        System.out.println(Thread.currentThread().getName() + ": 从redis获取");
        List<String> list = jedis.hvals("goods");
        List<Goods> goods = new LinkedList<>();
        list.forEach(e -> goods.add(JSONObject.parseObject(e, Goods.class)));
        jedis.close();
        return goods;
    }

    @Override
    public Goods getGoodsById(int goodsId) {
        Jedis jedis = jedisPool.getResource();
        Goods goods = JSONObject.parseObject(jedis.hget("goods", "" + goodsId), Goods.class);
        jedis.close();
        return goods;
    }

    @Override
    public boolean subNumber(int goodsId) {
        Jedis jedis = jedisPool.getResource();
        // 调用者已经判断商品id为goodsId的商品一定存在
        Goods goods = JSONObject.parseObject(jedis.hget("goods", goodsId + ""), Goods.class);
        goods.setNumber(goods.getNumber() - 1);
        long i = jedis.hset("goods", goodsId + "", JSONObject.toJSONString(goods));
        jedis.close();
        return i == 0;
    }

    private boolean getLock(Jedis jedis) {
        return "OK".equals(jedis.set("lock", "lock", "nx", "px", 3000L));
    }

    private void releaseLock(Jedis jedis) {
        jedis.del("lock");
    }
}
