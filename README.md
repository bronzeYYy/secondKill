# 简易秒杀系统

### 大致流程

#### 获取所有商品流程

![获取商品流程](E:\idea\Github\secondkill\获取商品流程.png)

#### 

#### 秒杀流程

![秒杀流程](E:\idea\Github\secondkill\秒杀流程.png)



### 目的

Service到redis有大量的用户（线程），需要保证访问mysql的线程数不会太多，不然数据库容易挂掉。

