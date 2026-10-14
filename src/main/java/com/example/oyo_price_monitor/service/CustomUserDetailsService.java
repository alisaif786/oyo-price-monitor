package com.example.oyo_price_monitor.service;


import com.example.oyo_price_monitor.entity.User;
import com.example.oyo_price_monitor.exception.ResourceNotFoundException;
import com.example.oyo_price_monitor.repository.UserRepository;

import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;



@Service
public class CustomUserDetailsService implements UserDetailsService {


    private final UserRepository userRepository;


    public CustomUserDetailsService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found!!!"
                ));

        if(user == null){
            throw new UsernameNotFoundException(
                    "User not found"
            );
        }
        return org.springframework.security.core.userdetails.User
                .builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles("USER")
                .build();

    }
}