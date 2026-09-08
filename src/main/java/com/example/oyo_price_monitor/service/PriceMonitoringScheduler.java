package com.example.oyo_price_monitor.service;

import com.example.oyo_price_monitor.entity.HotelMonitor;
import com.example.oyo_price_monitor.repository.HotelMonitorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PriceMonitoringScheduler {

    private static final Logger log =
            LoggerFactory.getLogger(PriceMonitoringScheduler.class);

    private final HotelMonitorRepository hotelMonitorRepository;
    private final HotelMonitorService hotelMonitorService;

    public PriceMonitoringScheduler(
            HotelMonitorRepository hotelMonitorRepository,
            HotelMonitorService hotelMonitorService) {

        this.hotelMonitorRepository = hotelMonitorRepository;
        this.hotelMonitorService = hotelMonitorService;
    }

    @Scheduled(fixedRate = 600000)
    public void monitorPrices() {

        log.info("========== AUTOMATIC PRICE CHECK STARTED ==========");

        List<HotelMonitor> monitors =
                hotelMonitorRepository.findByActiveTrue();

        log.info(
                "Found {} active hotel monitors",
                monitors.size()
        );

        for (HotelMonitor monitor : monitors) {

            try {

                log.info(
                        "Checking monitor ID: {}",
                        monitor.getId()
                );

                String result =
                        hotelMonitorService.checkPrice(monitor.getId());

                log.info(
                        "Monitor ID: {} check completed. Result: {}",
                        monitor.getId(),
                        result
                );

            } catch (Exception e) {

                log.error(
                        "Failed to check monitor ID: {}. Reason: {}",
                        monitor.getId(),
                        e.getMessage(),
                        e
                );
            }
        }

        log.info("========== AUTOMATIC PRICE CHECK COMPLETED ==========");
    }
}
