package com.example.oyo_price_monitor.service;

import com.example.oyo_price_monitor.dto.HotelMonitorRequest;
import com.example.oyo_price_monitor.entity.HotelMonitor;
import com.example.oyo_price_monitor.entity.PriceHistory;
import com.example.oyo_price_monitor.exception.ResourceNotFoundException;
import com.example.oyo_price_monitor.repository.HotelMonitorRepository;
import com.example.oyo_price_monitor.repository.PriceHistoryRepository;
import com.example.oyo_price_monitor.scraper.OyoPriceScraper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class HotelMonitorService {

    private static final Logger log =
            LoggerFactory.getLogger(HotelMonitorService.class);

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

        // Validate date rangeee, Like user cannot add checkout date before than checkkk in!!!
        if (!request.getCheckIn().isBefore(request.getCheckOut())) {
            throw new IllegalArgumentException(
                    "Check-in date must be before check-out date"
            );
        }

        HotelMonitor monitor = new HotelMonitor();

        monitor.setHotelUrl(request.getHotelUrl());
        monitor.setCheckIn(request.getCheckIn());
        monitor.setCheckOut(request.getCheckOut());
        monitor.setAdults(request.getAdults());
        monitor.setRooms(request.getRooms());
        monitor.setTargetPrice(request.getTargetPrice());

        monitor.setActive(true);

        // New monitor has not received an alert yettt
        monitor.setAlertSent(false);
        HotelMonitor savedMonitor = repository.save(monitor);
        log.info(
                "New hotel monitor created successfully. Monitor ID: {}, Target Price: ₹{}",
                savedMonitor.getId(),
                savedMonitor.getTargetPrice()
        );
        return savedMonitor;
    }

    public List<HotelMonitor> getAllMonitors() {
        log.debug("Fetching all hotel monitors");
        return repository.findAll();
    }

    public HotelMonitor getMonitor(Long id) {

        log.debug("Fetching hotel monitor with ID: {}", id);

        return repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Monitor with id " + id + " not found"
                        ));
    }

    public String checkPrice(Long monitorId) {

        log.info("Starting price check for monitor ID: {}", monitorId);

        HotelMonitor monitor = getMonitor(monitorId);

        // Defensive check for invalid database data
        if (monitor.getHotelUrl() == null ||
                monitor.getHotelUrl().isBlank()) {

            log.error(
                    "Monitor {} has an invalid hotel URL. Skipping price check.",
                    monitorId
            );

            return "Invalid hotel URL";
        }

        if (!monitor.isActive()) {
            log.warn(
                    "Price check skipped because monitor {} is inactive",
                    monitorId
            );
            return "Monitoring is inactive";
        }
        log.info(
                "Scraping OYO price for monitor {}. URL: {}",
                monitorId,
                monitor.getHotelUrl()
        );

        double currentPrice = oyoPriceScraper.getPrice(
                monitor.getHotelUrl(),
                monitor.getCheckIn(),
                monitor.getCheckOut(),
                monitor.getAdults(),
                monitor.getRooms()
        );

        log.info(
                "Price scraped successfully for monitor {}. Current price: ₹{}",
                monitorId,
                currentPrice
        );

        // Save price history
        PriceHistory priceHistory = new PriceHistory();

        priceHistory.setMonitor(monitor);
        priceHistory.setPrice(currentPrice);
        priceHistory.setCheckedAt(LocalDateTime.now());

        priceHistoryRepository.save(priceHistory);

        log.info(
                "Price history saved for monitor {}. Price: ₹{}",
                monitorId,
                currentPrice
        );

        // PRICE IS BELOW OR EQUAL TO TARGETTTT
        if (currentPrice <= monitor.getTargetPrice()) {

            log.info(
                    "Price target reached for monitor {}. Current: ₹{}, Target: ₹{}",
                    monitorId,
                    currentPrice,
                    monitor.getTargetPrice()
            );

            // Send alert onlyy if alert was not already senttt
            if (!monitor.isAlertSent()) {

                String message =
                        "🚨 OYO PRICE DROP!\n\n" +
                                "Hotel: " + monitor.getHotelUrl() + "\n" +
                                "Current Price: ₹" + currentPrice + "\n" +
                                "Target Price: ₹" + monitor.getTargetPrice() + "\n\n" +
                                "Book now before the price increases!";

                telegramNotificationService.sendMessage(message);

                // Mark alert as sentttt
                monitor.setAlertSent(true);
                repository.save(monitor);

                log.info(
                        "Telegram price-drop alert sent successfully for monitor {}. alertSent = true",
                        monitorId
                );

            } else {
                log.info(
                        "Alert already sent for monitor {}. Skipping duplicate notification.",
                        monitorId
                );
            }
            return "PRICE DROP! Current price: ₹"
                    + currentPrice
                    + ", Target price: ₹"
                    + monitor.getTargetPrice();
        }

        // PRICE IS ABOVE TARGET
        // Reset alert so a future price drop can trigger
        // a new notification
        if (monitor.isAlertSent()) {

            monitor.setAlertSent(false);
            repository.save(monitor);

            log.info(
                    "Price is above target for monitor {}. alertSent reset to false.",
                    monitorId
            );
        }

        log.info(
                "Price is still above target for monitor {}. Current: ₹{}, Target: ₹{}",
                monitorId,
                currentPrice,
                monitor.getTargetPrice()
        );

        return "Price is still above target. Current price: ₹"
                + currentPrice
                + ", Target price: ₹"
                + monitor.getTargetPrice();
    }

    public List<PriceHistory> getPriceHistory(Long monitorId) {

        log.debug("Fetching price history for monitor {}", monitorId);
        getMonitor(monitorId);

        return priceHistoryRepository
                .findByMonitorIdOrderByCheckedAtDesc(monitorId);
    }
}
