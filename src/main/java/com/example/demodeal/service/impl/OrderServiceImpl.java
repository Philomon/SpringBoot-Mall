package com.example.demodeal.service.impl;

import com.example.demodeal.domain.Goods;
import com.example.demodeal.domain.Order;
import com.example.demodeal.repository.OrderRepository;
import com.example.demodeal.service.OrderService;
import com.example.demodeal.thread.ThreadPoolManager;
import com.example.demodeal.utils.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;

public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ThreadPoolManager threadPoolManager;

    @Override
    public void save(Order order) {
       String objString = JsonUtil.obj2String(order);
       threadPoolManager.processOrders(objString);
    }
}
