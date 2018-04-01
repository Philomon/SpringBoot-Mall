package com.example.demodeal.service.impl;

import com.example.demodeal.repository.OrderRepository;
import com.example.demodeal.service.OrderItemService;
import com.example.demodeal.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;

public class OrderItemServiceImpl implements OrderItemService {

    @Autowired
    private OrderRepository orderRepository;



}
