package com.example.oyo_price_monitor.service;

import com.example.oyo_price_monitor.dto.HotelMonitorRequest;
import com.example.oyo_price_monitor.entity.HotelMonitor;
import com.example.oyo_price_monitor.repository.HotelMonitorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HotelMonitorService {

    private final HotelMonitorRepository repository;

    public HotelMonitorService(HotelMonitorRepository repository) {
        this.repository = repository;
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

        return repository.save(monitor);
    }

    public List<HotelMonitor> getAllMonitors() {
        return repository.findAll();
    }

    public HotelMonitor getMonitor(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Monitor not found"));
    }
}