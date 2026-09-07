package com.example.oyo_price_monitor.repository;

import com.example.oyo_price_monitor.entity.HotelMonitor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HotelMonitorRepository
        extends JpaRepository<HotelMonitor, Long> {
}