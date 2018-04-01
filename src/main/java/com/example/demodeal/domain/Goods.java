package com.example.demodeal.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;


//商品 秒杀 订单 单点登录 ES搜索引擎
@Entity
public class Goods {

    @Id
    @GeneratedValue
    private long id;// ID

    @Column
    private double price;// 商品价格


    @Column
    private int number;// 商品数量

    @Column
    private String name;

    public Goods() {
    }

    public Goods(double price, int number, String name) {

        this.price = price;
        this.number = number;
        this.name = name;
    }

    public long getId() {

        return id;
    }

    public void setId(long id) {
        this.id = id;
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

}
