package com.example.oyo_price_monitor.entity;

import jakarta.persistence.*;

@Entity
@Table(name="users")
public class User {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;
    private String password;
    private String telegramChatId;

    private boolean telegramConnected;
    public User(){

    }
    public Long getId(){
        return id;
    }

    public String getEmail(){
        return email;
    }


    public void setEmail(String email){
        this.email=email;
    }


    public String getPassword(){
        return password;
    }


    public void setPassword(String password){
        this.password=password;
    }


    public String getTelegramChatId(){
        return telegramChatId;
    }


    public void setTelegramChatId(String telegramChatId){
        this.telegramChatId=telegramChatId;
    }

    public boolean isTelegramConnected(){
        return telegramConnected;
    }


    public void setTelegramConnected(boolean telegramConnected){
        this.telegramConnected=telegramConnected;
    }

}