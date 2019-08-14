package cn.chen.second;

import cn.chen.second.dao.JedisDao;
import cn.chen.second.dao.impl.GoodsDaoImpl;
import cn.chen.second.dao.impl.JedisDaoImpl;
import cn.chen.second.model.Goods;
import cn.chen.second.service.GoodsService;
import cn.chen.second.service.impl.GoodsServiceImpl;
import cn.chen.second.util.BeanUtils;
import org.junit.Test;
import redis.clients.jedis.JedisPool;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class DBUtilsTest {
    @Test
    public void test() {
        /*for (Constructor<?> constructor : GoodsServiceImpl.class.getDeclaredConstructors()) {
            System.out.println(constructor.getName());
        }*/
        System.out.println(BeanUtils.getOrCreateInstance(GoodsServiceImpl.class).getAllGoods());
        /*try {
            System.out.println(this.getClass().getMethod("test1", String.class).getParameterTypes()[0].getName());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }*/
    }

    public void test1(String i) {
    }

    public void getI(Class c) {
        for (Method method : c.getMethods()) {
            if (!method.getName().startsWith("set")) {
                continue;
            }
            StringBuilder sb = new StringBuilder(method.getName().substring(3));
//            sb.setCharAt(0, (char) (sb.charAt(0) + 32));
            for (int i = 0; i < sb.length(); i++) {
                char ch = sb.charAt(i);
                if (ch >= 65 && ch <= 90) {
                    // 大写字母
                    sb.setCharAt(i, (char) (sb.charAt(i) + 32));
                    if (i == 0) {
                        continue;
                    }
                    sb.insert(i, '_');
                }
            }
            System.out.println(sb);
        }
    }

    @Test
    public void t() {
        System.out.println(GoodsServiceImpl.getInstance().killGoods(1, "213"));
    }

    @Test
    public void test11() {
        JedisPool jedisPool = new JedisPool();
        System.out.println(jedisPool.getClass().hashCode());
        System.out.println(JedisPool.class.hashCode());
        System.out.println(jedisPool.getClass() == JedisPool.class);
    }

    @Test
    public void jedisTest() {
        JedisDao jedisDao = JedisDaoImpl.getInstance();

        int nThread = 5000;
        CountDownLatch countDownLatch = new CountDownLatch(nThread);
        ExecutorService service = Executors.newFixedThreadPool(nThread);
        for (int i = 0; i < nThread; i++) {
            service.execute(() -> {
                jedisDao.getAllGoods();
                countDownLatch.countDown();
            });
        }
        service.shutdown();
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("完毕");
//        service.shutdown();
//        System.out.println(JedisDaoImpl.getInstance().getAllGoods());
    }
    @Test
    public void testt() {
        JedisDao jedisDao = JedisDaoImpl.getInstance();
        System.out.println(jedisDao.subNumber(2));
    }

    @Test
    public void kill() {
        GoodsService goodsService = GoodsServiceImpl.getInstance();
        long start = System.currentTimeMillis();
        int nThread = 5000;
        CountDownLatch countDownLatch = new CountDownLatch(nThread);
        ExecutorService service = Executors.newFixedThreadPool(nThread);
        for (int i = 0; i < nThread; i++) {
            service.execute(() -> {
                System.out.println(goodsService.killGoods(2, "1233"));
                countDownLatch.countDown();
            });
        }
        service.shutdown();
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("完毕，耗时: " + (System.currentTimeMillis() - start));
    }
}