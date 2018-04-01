# SpringBoot-Mall


### 主要功能：提供了商城系统的后端Api，不包含前端页面，体现了前后端解耦分离的思想。实现了商品，购物车，订单系统，单点登录系统等4个功能模块
### 技术要点：
使用了线程池+队列解决“订单并发”问题

使用了Redis解决集群间session存储问题

使用了Spring Security 以及 Spring Security OAuth 实现基于JWT的单点登录

设计了实时消息服务，使用消息队列kafka实现，用于避免数据库操作中的分布式事务 

使用ElasticSearch实现站内搜索引擎

### TODO
使用了常见的设计模式对项目结构进行重构
