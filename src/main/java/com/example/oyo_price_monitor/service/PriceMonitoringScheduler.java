package com.example.oyo_price_monitor.service;

import com.example.oyo_price_monitor.entity.HotelMonitor;
import com.example.oyo_price_monitor.repository.HotelMonitorRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PriceMonitoringScheduler {

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

        System.out.println("\n========== AUTOMATIC PRICE CHECK ==========");

        List<HotelMonitor> monitors =
                hotelMonitorRepository.findByActiveTrue();

        for (HotelMonitor monitor : monitors) {

            try {

                System.out.println(
                        "Checking monitor ID: " + monitor.getId()
                );

                String result =
                        hotelMonitorService.checkPrice(monitor.getId());

                System.out.println(result);

            } catch (Exception e) {

                System.out.println(
                        "Failed to check monitor ID: "
                                + monitor.getId()
                );

                System.out.println(
                        "Reason: " + e.getMessage()
                );
            }
        }
    }
}