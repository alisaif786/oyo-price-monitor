package com.example.oyo_price_monitor.config;

import com.example.oyo_price_monitor.bot.OyoTelegramBot;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
public class TelegramBotConfig {

    @Bean
    public TelegramBotsApi telegramBotsApi(OyoTelegramBot bot) throws Exception {

        TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
        api.registerBot(bot);

        System.out.println("========== TELEGRAM BOT REGISTERED ==========");

        return api;
    }
}