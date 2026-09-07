package com.example.oyo_price_monitor.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class TelegramNotificationService {

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.chat.id}")
    private String chatId;

    private final RestTemplate restTemplate = new RestTemplate();

    public void sendMessage(String message) {

        String url = "https://api.telegram.org/bot"
                + botToken
                + "/sendMessage";

        Map<String, String> request = Map.of(
                "chat_id", chatId,
                "text", message
        );

        restTemplate.postForObject(
                url,
                request,
                String.class
        );
    }
}