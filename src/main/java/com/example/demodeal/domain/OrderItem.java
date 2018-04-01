package com.example.demodeal.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class OrderItem {

    @Id
    @GeneratedValue
    private long id;// ID

    @Column
    private long orderID;// 订单号

    @Column
    private double price;// 商品单价

    @Column
    private int number;// 商品数量

    @Column
    private String name;

    public OrderItem() {
    }

    public long getId() {

        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getOrderID() {
        return orderID;
    }

    public void setOrderID(long orderID) {
        this.orderID = orderID;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public OrderItem(long orderID, double price, int number, String name) {

        this.orderID = orderID;
        this.price = price;
        this.number = number;
        this.name = name;
    }



}
