package com.example.demodeal.service;

import com.example.demodeal.domain.Goods;
import java.util.List;

public interface SearchService {
    /**
     * 索引目标商品
     * @param goodsId
     */
    void index(Long goodsId);

    /**
     * 移除商品索引
     * @param goodsId
     */
    void remove(Long goodsId);

    /**
     * 根据商品名索引商品
     */
    List<Goods> findByName(String name);

    /**
     * 根据id索引商品
     */
    List<Goods> findById(Long goodsId);


}
