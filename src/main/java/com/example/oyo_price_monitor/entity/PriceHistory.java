
        package com.example.oyo_price_monitor.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "price_history")
public class PriceHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "monitor_id", nullable = false)
    private HotelMonitor monitor;

    @Column(nullable = false)
    private double price;

    @Column(nullable = false)
    private LocalDateTime checkedAt;

    public Long getId() {
        return id;
    }

    public HotelMonitor getMonitor() {
        return monitor;
    }

    public void setMonitor(HotelMonitor monitor) {
        this.monitor = monitor;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public LocalDateTime getCheckedAt() {
        return checkedAt;
    }

    public void setCheckedAt(LocalDateTime checkedAt) {
        this.checkedAt = checkedAt;
    }
}
