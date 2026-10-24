package com.example.oyo_price_monitor.controller;

import com.example.oyo_price_monitor.service.TelegramService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/telegram")
@RequiredArgsConstructor
public class TelegramController {

    private final TelegramService telegramService;

    @PostMapping("/connect")
    public ResponseEntity<Map<String, String>> connectTelegram() {

        String token = telegramService.generateConnectionToken();

        return ResponseEntity.ok(
                Map.of("token", token)
        );
    }
}