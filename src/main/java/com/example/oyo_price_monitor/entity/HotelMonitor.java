package com.example.oyo_price_monitor.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "hotel_monitors")
@Getter
@Setter
@NoArgsConstructor
public class HotelMonitor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String hotelUrl;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private int adults;
    private int rooms;
    private double targetPrice;
    private boolean active;

    // forrr Prevent duplicate Telegram alerts
    private boolean alertSent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    @JsonBackReference
    private User user;

}