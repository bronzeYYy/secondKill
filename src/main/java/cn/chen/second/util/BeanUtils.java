package cn.chen.second.util;

import redis.clients.jedis.JedisPool;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ConcurrentHashMap;

public final class BeanUtils {
    private static ConcurrentHashMap<Object, Object> beanMap = new ConcurrentHashMap<>();
    public static <T> T getOrCreateInstance(Class<T> o) {
        if (beanMap.get(o) == null) {
            synchronized(o) {
                if (beanMap.get(o) == null) {
                    try {
                        Constructor constructor = o.getDeclaredConstructor();
                        constructor.setAccessible(true);
                        beanMap.put(o, constructor.newInstance());
                        constructor.setAccessible(false);
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return (T) beanMap.get(o);
    }
    public static JedisPool getOrCreateJedisPool() {
        if (beanMap.get(JedisPool.class) == null) {
            synchronized(JedisPool.class) {
                if (beanMap.get(JedisPool.class) == null) {
                    beanMap.put(JedisPool.class, new JedisPool("192.168.42.218"));
                }
            }
        }
        return (JedisPool) beanMap.get(JedisPool.class);
    }
}
