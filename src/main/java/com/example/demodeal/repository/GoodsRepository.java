package com.example.demodeal.repository;

import com.example.demodeal.domain.Goods;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GoodsRepository extends JpaRepository<Goods,Long> {
    public List<Goods> findByName(String name);

}
