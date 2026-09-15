package com.example.oyo_price_monitor.repository;

import com.example.oyo_price_monitor.entity.PriceHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PriceHistoryRepository
        extends JpaRepository<PriceHistory, Long> {

    List<PriceHistory> findByMonitorIdOrderByCheckedAtDesc(Long monitorId);

    void deleteByMonitorId(Long monitorId);
}