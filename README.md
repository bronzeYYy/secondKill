# 简易秒杀系统

### 大致流程

#### 秒杀流程

![1565778157295](C:\Users\17691\AppData\Roaming\Typora\typora-user-images\1565778157295.png)

#### 获取所有商品流程

![1565778400338](C:\Users\17691\AppData\Roaming\Typora\typora-user-images\1565778400338.png)

### 目的

Service到redis有大量的用户（线程），需要保证访问mysql的线程数不会太多，不然数据库容易挂掉。

