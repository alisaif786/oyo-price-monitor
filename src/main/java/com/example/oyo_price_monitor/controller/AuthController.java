package com.example.oyo_price_monitor.controller;

import com.example.oyo_price_monitor.entity.User;
import com.example.oyo_price_monitor.repository.UserRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public String registerUser(@RequestBody User user){
        userRepository.save(user);
        return "User registered successfully";
    }

}
