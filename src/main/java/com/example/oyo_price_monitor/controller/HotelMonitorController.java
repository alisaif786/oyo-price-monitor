package com.example.oyo_price_monitor.controller;

import com.example.oyo_price_monitor.dto.HotelMonitorRequest;
import com.example.oyo_price_monitor.entity.HotelMonitor;
import com.example.oyo_price_monitor.entity.PriceHistory;
import com.example.oyo_price_monitor.entity.User;
import com.example.oyo_price_monitor.service.HotelMonitorService;
import com.example.oyo_price_monitor.service.TelegramNotificationService;
import com.example.oyo_price_monitor.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/monitors")
public class HotelMonitorController {

    private final HotelMonitorService service;
    private final TelegramNotificationService telegramNotificationService;
    private final UserService userService;

    public HotelMonitorController(
            HotelMonitorService service,
            TelegramNotificationService telegramNotificationService, UserService userService) {

        this.service = service;
        this.telegramNotificationService = telegramNotificationService;
        this.userService = userService;
    }
    @PostMapping
    public HotelMonitor addMonitor(@Valid @RequestBody HotelMonitorRequest request) {

        User user = userService.getLoggedInUser();

        return service.addMonitor(request, user);
    }

    //Only User who logged in will get thier monitoring
    @GetMapping
    public List<HotelMonitor> getAllMonitors() {
        User user=userService.getLoggedInUser();
        return service.getAllMonitors(user);
    }

    @GetMapping("/{id}/check")
    public String checkPrice(@PathVariable Long id) {
        User user=userService.getLoggedInUser();
        return service.checkPrice(id, user);
    }

    @GetMapping("/{id}/history")
    public List<PriceHistory> getPriceHistory(
            @PathVariable Long id) {
        User user=userService.getLoggedInUser();
        return service.getPriceHistory(id, user);
    }
    @GetMapping("/test-telegram")
    public String testTelegram() {
        User user=userService.getLoggedInUser();
        telegramNotificationService.sendMessage(
                "🚨 OYO Price Monitor Test\n\n" +
                        "Telegram notification is working successfully! ✅", user.getTelegramChatId()
        );

        return "Telegram message sent!";
    }
    @GetMapping("/{id}")
    public HotelMonitor getMonitor(@PathVariable Long id) {
        User user=userService.getLoggedInUser();
        return service.getMonitor(id, user);
    }

    @DeleteMapping("/{id}")
    public String deleteMonitor(@PathVariable Long id) {
        User user=userService.getLoggedInUser();
        service.deleteMonitor(id, user);

        return "Monitor deleted successfully";
    }
}
