package com.example.demodeal.thread;

import com.example.demodeal.domain.Order;
import com.example.demodeal.repository.OrderRepository;
import com.example.demodeal.utils.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.UUID;


@Component
@Scope("prototype")//spring 多例
public class DBThread implements Runnable {


    @Autowired
    private OrderRepository orderRepository;

    private String msg;
    private Logger log = LoggerFactory.getLogger(DBThread.class);


    @Override
    public void run() {
        Order order = JsonUtil.string2Obj(msg,Order.class);
        orderRepository.save(order);
    }


    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}