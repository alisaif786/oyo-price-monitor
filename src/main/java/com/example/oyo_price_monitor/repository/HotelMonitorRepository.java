package com.example.oyo_price_monitor.repository;

import com.example.oyo_price_monitor.entity.HotelMonitor;
import com.example.oyo_price_monitor.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HotelMonitorRepository
        extends JpaRepository<HotelMonitor, Long> {

    List<HotelMonitor> findByActiveTrue();

    List<HotelMonitor> findByUser(User user);

    Optional<HotelMonitor> findByIdAndUser(Long id, User user);
}