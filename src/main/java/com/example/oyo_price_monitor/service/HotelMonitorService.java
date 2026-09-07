package com.example.oyo_price_monitor.service;

import com.example.oyo_price_monitor.dto.HotelMonitorRequest;
import com.example.oyo_price_monitor.entity.HotelMonitor;
import com.example.oyo_price_monitor.entity.PriceHistory;
import com.example.oyo_price_monitor.repository.HotelMonitorRepository;
import com.example.oyo_price_monitor.repository.PriceHistoryRepository;
import com.example.oyo_price_monitor.scraper.OyoPriceScraper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class HotelMonitorService {

    private final HotelMonitorRepository repository;
    private final PriceHistoryRepository priceHistoryRepository;
    private final OyoPriceScraper oyoPriceScraper;
    private final TelegramNotificationService telegramNotificationService;

    public HotelMonitorService(
            HotelMonitorRepository repository,
            PriceHistoryRepository priceHistoryRepository,
            OyoPriceScraper oyoPriceScraper,
            TelegramNotificationService telegramNotificationService) {

        this.repository = repository;
        this.priceHistoryRepository = priceHistoryRepository;
        this.oyoPriceScraper = oyoPriceScraper;
        this.telegramNotificationService = telegramNotificationService;
    }

    public HotelMonitor addMonitor(HotelMonitorRequest request) {

        HotelMonitor monitor = new HotelMonitor();

        monitor.setHotelUrl(request.getHotelUrl());
        monitor.setCheckIn(request.getCheckIn());
        monitor.setCheckOut(request.getCheckOut());
        monitor.setAdults(request.getAdults());
        monitor.setRooms(request.getRooms());
        monitor.setTargetPrice(request.getTargetPrice());

        monitor.setActive(true);

        // New monitor has not received an alert yet
        monitor.setAlertSent(false);

        return repository.save(monitor);
    }

    public List<HotelMonitor> getAllMonitors() {
        return repository.findAll();
    }

    public HotelMonitor getMonitor(Long id) {

        return repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Monitor not found"));
    }

    public String checkPrice(Long monitorId) {

        HotelMonitor monitor = getMonitor(monitorId);

        if (!monitor.isActive()) {
            return "Monitoring is inactive";
        }

        double currentPrice = oyoPriceScraper.getPrice(
                monitor.getHotelUrl(),
                monitor.getCheckIn(),
                monitor.getCheckOut(),
                monitor.getAdults(),
                monitor.getRooms()
        );

        // Save price history
        PriceHistory priceHistory = new PriceHistory();

        priceHistory.setMonitor(monitor);
        priceHistory.setPrice(currentPrice);
        priceHistory.setCheckedAt(LocalDateTime.now());

        priceHistoryRepository.save(priceHistory);

        System.out.println(
                "Price saved in history: ₹" + currentPrice
        );

        // ==========================================
        // PRICE IS BELOW OR EQUAL TO TARGET
        // ==========================================

        if (currentPrice <= monitor.getTargetPrice()) {

            System.out.println(
                    "🚨 PRICE DROP! Current price ₹"
                            + currentPrice
                            + " <= Target price ₹"
                            + monitor.getTargetPrice()
            );

            // Send alert ONLY if alert was not already sent
            if (!monitor.isAlertSent()) {

                String message =
                        "🚨 OYO PRICE DROP!\n\n" +
                                "Hotel: " + monitor.getHotelUrl() + "\n" +
                                "Current Price: ₹" + currentPrice + "\n" +
                                "Target Price: ₹" + monitor.getTargetPrice() + "\n\n" +
                                "Book now before the price increases!";

                telegramNotificationService.sendMessage(message);

                // Mark alert as sent
                monitor.setAlertSent(true);
                repository.save(monitor);

                System.out.println(
                        "✅ Telegram alert sent. alertSent = true"
                );

            } else {

                System.out.println(
                        "ℹ️ Alert already sent. Skipping duplicate notification."
                );
            }

            return "PRICE DROP! Current price: ₹"
                    + currentPrice
                    + ", Target price: ₹"
                    + monitor.getTargetPrice();
        }

        // ==========================================
        // PRICE IS ABOVE TARGET
        // ==========================================

        // Reset alert so a future price drop can trigger
        // a new notification
        if (monitor.isAlertSent()) {

            monitor.setAlertSent(false);
            repository.save(monitor);

            System.out.println(
                    "🔄 Price is above target. alertSent reset to false."
            );
        }

        return "Price is still above target. Current price: ₹"
                + currentPrice
                + ", Target price: ₹"
                + monitor.getTargetPrice();
    }

    public List<PriceHistory> getPriceHistory(Long monitorId) {

        getMonitor(monitorId);

        return priceHistoryRepository
                .findByMonitorIdOrderByCheckedAtDesc(monitorId);
    }
}