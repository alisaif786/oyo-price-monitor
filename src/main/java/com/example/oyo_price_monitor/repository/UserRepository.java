package com.example.oyo_price_monitor.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import com.example.oyo_price_monitor.entity.User;

import java.util.Optional;


public interface UserRepository
        extends JpaRepository<User,Long>{


    Optional<User> findByEmail(String email);

}