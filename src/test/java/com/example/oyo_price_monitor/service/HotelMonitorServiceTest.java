package com.example.oyo_price_monitor.service;

import com.example.oyo_price_monitor.entity.HotelMonitor;
import com.example.oyo_price_monitor.entity.PriceHistory;
import com.example.oyo_price_monitor.exception.ResourceNotFoundException;
import com.example.oyo_price_monitor.repository.HotelMonitorRepository;
import com.example.oyo_price_monitor.repository.PriceHistoryRepository;
import com.example.oyo_price_monitor.scraper.OyoPriceScraper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HotelMonitorServiceTest {

    @Mock
    private HotelMonitorRepository repository;

    @Mock
    private PriceHistoryRepository priceHistoryRepository;

    @Mock
    private OyoPriceScraper oyoPriceScraper;

    @Mock
    private TelegramNotificationService telegramNotificationService;

    @InjectMocks
    private HotelMonitorService hotelMonitorService;

    @Test
    void shouldThrowExceptionWhenMonitorDoesNotExist() {

        when(repository.findById(999L))
                .thenReturn(java.util.Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> hotelMonitorService.getMonitor(999L)
        );

        verify(repository).findById(999L);
    }

    @Test
    void shouldReturnInactiveWhenMonitorIsInactive() {

        HotelMonitor monitor = createMonitor();

        monitor.setActive(false);

        when(repository.findById(1L))
                .thenReturn(java.util.Optional.of(monitor));

        String result = hotelMonitorService.checkPrice(1L);

        assertEquals(
                "Monitoring is inactive",
                result
        );

        verify(oyoPriceScraper, never()).getPrice(
                any(),
                any(LocalDate.class),
                any(LocalDate.class),
                anyInt(),
                anyInt()
        );
    }

    @Test
    void shouldReturnInvalidUrlWhenHotelUrlIsNull() {

        HotelMonitor monitor = createMonitor();

        monitor.setHotelUrl(null);

        when(repository.findById(1L))
                .thenReturn(java.util.Optional.of(monitor));

        String result = hotelMonitorService.checkPrice(1L);

        assertEquals(
                "Invalid hotel URL",
                result
        );

        verify(oyoPriceScraper, never()).getPrice(
                any(),
                any(LocalDate.class),
                any(LocalDate.class),
                anyInt(),
                anyInt()
        );
    }

    @Test
    void shouldReturnInvalidUrlWhenHotelUrlIsBlank() {

        HotelMonitor monitor = createMonitor();

        monitor.setHotelUrl("");

        when(repository.findById(1L))
                .thenReturn(java.util.Optional.of(monitor));

        String result = hotelMonitorService.checkPrice(1L);

        assertEquals(
                "Invalid hotel URL",
                result
        );

        verify(oyoPriceScraper, never()).getPrice(
                any(),
                any(LocalDate.class),
                any(LocalDate.class),
                anyInt(),
                anyInt()
        );
    }

    @Test
    void shouldSendTelegramAlertWhenPriceIsBelowTarget() {

        HotelMonitor monitor = createMonitor();

        monitor.setTargetPrice(1600.0);
        monitor.setAlertSent(false);

        when(repository.findById(1L))
                .thenReturn(java.util.Optional.of(monitor));

        when(oyoPriceScraper.getPrice(
                any(),
                any(LocalDate.class),
                any(LocalDate.class),
                anyInt(),
                anyInt()
        )).thenReturn(1502.0);

        String result =
                hotelMonitorService.checkPrice(1L);

        assertTrue(
                result.contains("PRICE DROP")
        );

        assertTrue(
                monitor.isAlertSent()
        );

        verify(priceHistoryRepository)
                .save(any(PriceHistory.class));

        verify(telegramNotificationService)
                .sendMessage(any());

        verify(repository)
                .save(monitor);
    }

    @Test
    void shouldNotSendDuplicateTelegramAlert() {

        HotelMonitor monitor = createMonitor();

        monitor.setTargetPrice(1600.0);

        // Alert already sent previously
        monitor.setAlertSent(true);

        when(repository.findById(1L))
                .thenReturn(java.util.Optional.of(monitor));

        when(oyoPriceScraper.getPrice(
                any(),
                any(LocalDate.class),
                any(LocalDate.class),
                anyInt(),
                anyInt()
        )).thenReturn(1502.0);

        hotelMonitorService.checkPrice(1L);

        verify(
                telegramNotificationService,
                never()
        ).sendMessage(any());

        verify(priceHistoryRepository)
                .save(any(PriceHistory.class));
    }

    @Test
    void shouldResetAlertWhenPriceGoesAboveTarget() {

        HotelMonitor monitor = createMonitor();

        monitor.setTargetPrice(1600.0);

        // Previous alert was sent
        monitor.setAlertSent(true);

        when(repository.findById(1L))
                .thenReturn(java.util.Optional.of(monitor));

        when(oyoPriceScraper.getPrice(
                any(),
                any(LocalDate.class),
                any(LocalDate.class),
                anyInt(),
                anyInt()
        )).thenReturn(1800.0);

        hotelMonitorService.checkPrice(1L);

        assertFalse(
                monitor.isAlertSent()
        );

        verify(repository)
                .save(monitor);

        verify(
                telegramNotificationService,
                never()
        ).sendMessage(any());
    }

    @Test
    void shouldSavePriceHistoryWhenPriceIsChecked() {

        HotelMonitor monitor = createMonitor();

        monitor.setTargetPrice(1600.0);
        monitor.setAlertSent(false);

        when(repository.findById(1L))
                .thenReturn(java.util.Optional.of(monitor));

        when(oyoPriceScraper.getPrice(
                any(),
                any(LocalDate.class),
                any(LocalDate.class),
                anyInt(),
                anyInt()
        )).thenReturn(1700.0);

        hotelMonitorService.checkPrice(1L);

        verify(priceHistoryRepository)
                .save(any(PriceHistory.class));
    }

    private HotelMonitor createMonitor() {

        HotelMonitor monitor = new HotelMonitor();

        monitor.setHotelUrl(
                "https://www.oyorooms.com/192673/"
        );

        monitor.setCheckIn(
                LocalDate.of(2026, 9, 10)
        );

        monitor.setCheckOut(
                LocalDate.of(2026, 9, 11)
        );

        monitor.setAdults(2);

        monitor.setRooms(1);

        monitor.setTargetPrice(1600.0);

        monitor.setActive(true);

        monitor.setAlertSent(false);

        return monitor;
    }
}
