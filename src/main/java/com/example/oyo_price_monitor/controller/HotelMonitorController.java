
        package com.example.oyo_price_monitor.controller;

import com.example.oyo_price_monitor.dto.HotelMonitorRequest;
import com.example.oyo_price_monitor.entity.HotelMonitor;
import com.example.oyo_price_monitor.entity.PriceHistory;
import com.example.oyo_price_monitor.service.HotelMonitorService;
import com.example.oyo_price_monitor.service.TelegramNotificationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/monitors")
public class HotelMonitorController {

    private final HotelMonitorService service;
    private final TelegramNotificationService telegramNotificationService;

    public HotelMonitorController(
            HotelMonitorService service,
            TelegramNotificationService telegramNotificationService) {

        this.service = service;
        this.telegramNotificationService = telegramNotificationService;
    }
    @PostMapping
    public HotelMonitor addMonitor(
            @Valid @RequestBody HotelMonitorRequest request) {

        return service.addMonitor(request);
    }

    @GetMapping
    public List<HotelMonitor> getAllMonitors() {

        return service.getAllMonitors();
    }

    @GetMapping("/{id}/check")
    public String checkPrice(@PathVariable Long id) {
        return service.checkPrice(id);
    }

    @GetMapping("/{id}/history")
    public List<PriceHistory> getPriceHistory(
            @PathVariable Long id) {

        return service.getPriceHistory(id);
    }
    @GetMapping("/test-telegram")
    public String testTelegram() {

        telegramNotificationService.sendMessage(
                "🚨 OYO Price Monitor Test\n\n" +
                        "Telegram notification is working successfully! ✅"
        );

        return "Telegram message sent!";
    }
}
