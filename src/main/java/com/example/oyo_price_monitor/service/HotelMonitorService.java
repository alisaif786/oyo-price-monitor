package com.example.oyo_price_monitor.service;

import com.example.oyo_price_monitor.dto.HotelMonitorRequest;
import com.example.oyo_price_monitor.entity.HotelMonitor;
import com.example.oyo_price_monitor.entity.PriceHistory;
import com.example.oyo_price_monitor.entity.User;
import com.example.oyo_price_monitor.exception.ResourceNotFoundException;
import com.example.oyo_price_monitor.repository.HotelMonitorRepository;
import com.example.oyo_price_monitor.repository.PriceHistoryRepository;
import com.example.oyo_price_monitor.scraper.OyoPriceScraper;
import org.springframework.transaction.annotation.Transactional;
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

    public HotelMonitor addMonitor(HotelMonitorRequest request, User user) {

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

        monitor.setUser(user);

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
    //chnaged method so that every user can see only his monitoring
    public List<HotelMonitor> getAllMonitors(User user) {
        return repository.findByUser(user);
    }

    public HotelMonitor getMonitor(Long id, User user) {

        return repository.findByIdAndUser(id, user)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Monitor with "+id +" not found"
                        ));
    }

    public String checkPrice(Long monitorId, User user) {

        HotelMonitor monitor = getMonitor(monitorId, user);

        return checkPriceInternal(monitor);

    }
    public String checkPriceInternal(HotelMonitor monitor) {

        log.info("Starting price check for monitor ID: {}", monitor.getId());

        if (monitor.getHotelUrl() == null || monitor.getHotelUrl().isBlank()) {

            log.error(
                    "Monitor {} has an invalid hotel URL. Skipping price check.",
                    monitor.getId()
            );

            return "Invalid hotel URL";
        }

        if (!monitor.isActive()) {

            log.warn(
                    "Price check skipped because monitor {} is inactive",
                    monitor.getId()
            );

            return "Monitoring is inactive";
        }

        log.info(
                "Scraping OYO price for monitor {}. URL: {}",
                monitor.getId(),
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
                monitor.getId(),
                currentPrice
        );

        PriceHistory history = new PriceHistory();
        history.setMonitor(monitor);
        history.setPrice(currentPrice);
        history.setCheckedAt(LocalDateTime.now());

        priceHistoryRepository.save(history);

        if (currentPrice <= monitor.getTargetPrice()) {

            if (!monitor.isAlertSent()) {

                User owner = monitor.getUser();
                if (owner != null
                        && owner.isTelegramConnected()
                        && owner.getTelegramChatId() != null
                        && !owner.getTelegramChatId().isBlank()) {

                    String message =
                            "🚨 OYO PRICE DROP!\n\n" +
                                    "Hotel: " + monitor.getHotelUrl() + "\n" +
                                    "Current Price: ₹" + currentPrice + "\n" +
                                    "Target Price: ₹" + monitor.getTargetPrice() + "\n\n" +
                                    "Book now before the price increases!";

                    telegramNotificationService.sendMessage(
                            owner.getTelegramChatId(),
                            message
                    );
                }
                monitor.setAlertSent(true);
                repository.save(monitor);
            }

            return "PRICE DROP! Current price: ₹"
                    + currentPrice
                    + ", Target price: ₹"
                    + monitor.getTargetPrice();
        }

        if (monitor.isAlertSent()) {
            monitor.setAlertSent(false);
            repository.save(monitor);
        }

        return "Price is still above target. Current price: ₹"
                + currentPrice
                + ", Target price: ₹"
                + monitor.getTargetPrice();
    }
    public List<PriceHistory> getPriceHistory(Long monitorId, User user) {

        log.debug("Fetching price history for monitor {}", monitorId);
        getMonitor(monitorId, user);

        return priceHistoryRepository
                .findByMonitorIdOrderByCheckedAtDesc(monitorId);
    }

    @Transactional
    public void deleteMonitor(Long id, User user) {
        HotelMonitor monitor = getMonitor(id, user);

        // Delete price history first because it references the monitor
        priceHistoryRepository.deleteByMonitorId(id);

        // Now delete the monitorrr
        repository.delete(monitor);

        log.info("Hotel monitor deleted successfully. Monitor ID: {}", id);
    }
    public List<HotelMonitor> getActiveMonitors() {
        return repository.findByActiveTrue();
    }
}
