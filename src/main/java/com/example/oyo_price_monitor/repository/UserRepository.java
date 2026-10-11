package com.example.oyo_price_monitor.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import com.example.oyo_price_monitor.entity.User;


public interface UserRepository
        extends JpaRepository<User,Long>{


    User findByEmail(String email);

}