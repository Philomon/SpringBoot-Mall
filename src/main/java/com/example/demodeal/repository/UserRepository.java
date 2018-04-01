package com.example.demodeal.repository;

import com.example.demodeal.domain.Goods;
import com.example.demodeal.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User,Long> {

    //jpa 如何返回一个值的
    public User findByName(String name);
}
