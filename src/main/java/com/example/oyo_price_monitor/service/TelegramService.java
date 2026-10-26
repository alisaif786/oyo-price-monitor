package com.example.oyo_price_monitor.service;

import com.example.oyo_price_monitor.entity.User;
import com.example.oyo_price_monitor.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TelegramService {

    private final UserService userService;
    private final UserRepository userRepository;

    public String generateConnectionToken() {

        User user = userService.getLoggedInUser();

        String token = UUID.randomUUID().toString();

        user.setTelegramToken(token);

        userRepository.save(user);

        return token;
    }

    public boolean connectUser(String token, String chatId) {

        User user = userRepository.findByTelegramToken(token)
                .orElse(null);
        if (user == null) {
            return false;
        }
        user.setTelegramChatId(chatId);
        user.setTelegramConnected(true);
        user.setTelegramToken(null);
        userRepository.save(user);
        return true;
    }
    public boolean isTelegramConnected() {
        User user = userService.getLoggedInUser();
        return user.isTelegramConnected();
    }
}