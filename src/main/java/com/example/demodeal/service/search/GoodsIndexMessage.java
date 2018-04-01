package com.example.demodeal.service.search;

public class GoodsIndexMessage {

    public static final String INDEX = "index";
    public static final String REMOVE = "remove";

    public static final int MAX_RETRY = 3;

    private Long GoodsID;
    private String operation;
    private int retry = 0;


    public GoodsIndexMessage() {
    }

    public GoodsIndexMessage(Long goodsId, String operation, int retry) {
        this.GoodsID = goodsId;
        this.operation = operation;
        this.retry = retry;
    }

    public Long getGoodsID() {
        return GoodsID;
    }

    public void setGoodsID(Long goodsID) {
        GoodsID = goodsID;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public int getRetry() {
        return retry;
    }

    public void setRetry(int retry) {
        this.retry = retry;
    }

}
