package com.example.oyo_price_monitor.bot;

import com.example.oyo_price_monitor.service.TelegramService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class OyoTelegramBot extends TelegramLongPollingBot {

    private static final Logger log =
            LoggerFactory.getLogger(OyoTelegramBot.class);

    private final TelegramService telegramService;

    @Value("${telegram.bot.username}")
    private String botUsername;

    public OyoTelegramBot(
            @Value("${telegram.bot.token}") String botToken,
            TelegramService telegramService) {

        super(botToken);
        this.telegramService = telegramService;

        log.info("Telegram bot created successfully");
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public void onUpdateReceived(Update update) {

        // Ignore updates that are not text msgs
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }

        String text = update.getMessage().getText();
        String chatId = update.getMessage().getChatId().toString();

        log.info("Received Telegram message: {}", text);

        if (text.startsWith("/connect ")) {

            String token = text.substring("/connect ".length()).trim();

            boolean connected = telegramService.connectUser(token, chatId);

            String reply = connected
                    ? "✅ Your Telegram account has been connected successfully!"
                    : "❌ Invalid or expired connection token.";

            SendMessage message = new SendMessage(chatId, reply);

            try {
                execute(message);

                if (connected) {
                    log.info("Telegram account connected successfully. Chat ID: {}", chatId);
                } else {
                    log.warn("Invalid Telegram connection token received.");
                }

            } catch (TelegramApiException e) {
                log.error("Failed to send Telegram reply", e);
            }
        }
    }
}