package com.example.oyo_price_monitor.controller;

import com.example.oyo_price_monitor.entity.User;
import com.example.oyo_price_monitor.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public String registerUser(@RequestBody User user){

        //This PasswordEncoded is a interface its child class is BCryptPasswordEncoder
        // and with this encode method password will be converted into Hash form so that no one can understand
        String encodedPassword =
                passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        userRepository.save(user);
        return "User registered successfully";
    }

}
