package com.example.oyo_price_monitor.controller;

import com.example.oyo_price_monitor.scraper.OyoPriceScraper;
import com.example.oyo_price_monitor.dto.HotelMonitorRequest;
import com.example.oyo_price_monitor.entity.HotelMonitor;
import com.example.oyo_price_monitor.service.HotelMonitorService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/monitors")
public class HotelMonitorController {

    private final OyoPriceScraper scraper;

    private final HotelMonitorService service;

    public HotelMonitorController(
            HotelMonitorService service,
            OyoPriceScraper scraper) {

        this.service = service;
        this.scraper = scraper;
    }

    @PostMapping
    public HotelMonitor addMonitor(
            @RequestBody HotelMonitorRequest request) {

        return service.addMonitor(request);
    }

    @GetMapping
    public List<HotelMonitor> getAllMonitors() {

        return service.getAllMonitors();
    }
    @GetMapping("/{id}/check")
    public String checkPrice(@PathVariable Long id) {

        HotelMonitor monitor = service.getMonitor(id);

        double price = scraper.getPrice(
                monitor.getHotelUrl(),
                monitor.getCheckIn(),
                monitor.getCheckOut(),
                monitor.getAdults(),
                monitor.getRooms()
        );

        return "Current price: ₹" + price;
    }
}