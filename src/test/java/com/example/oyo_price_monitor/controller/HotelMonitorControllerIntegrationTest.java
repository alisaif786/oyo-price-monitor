package com.example.oyo_price_monitor.controller;

import com.example.oyo_price_monitor.entity.HotelMonitor;
import com.example.oyo_price_monitor.repository.HotelMonitorRepository;
import com.example.oyo_price_monitor.repository.PriceHistoryRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@SpringBootTest
@AutoConfigureMockMvc
class HotelMonitorControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private HotelMonitorRepository hotelMonitorRepository;

    @Autowired
    private PriceHistoryRepository priceHistoryRepository;

    @BeforeEach
    void cleanDatabase() {

        priceHistoryRepository.deleteAll();
        hotelMonitorRepository.deleteAll();
    }

    @Test
    void shouldCreateHotelMonitorSuccessfully() throws Exception {

        String requestBody = """
                {
                    "hotelUrl": "https://www.oyorooms.com/192673/",
                    "checkIn": "2026-09-20",
                    "checkOut": "2026-09-21",
                    "adults": 2,
                    "rooms": 1,
                    "targetPrice": 1600
                }
                """;

        mockMvc.perform(
                        post("/api/monitors")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hotelUrl")
                        .value("https://www.oyorooms.com/192673/"))
                .andExpect(jsonPath("$.adults")
                        .value(2))
                .andExpect(jsonPath("$.rooms")
                        .value(1))
                .andExpect(jsonPath("$.targetPrice")
                        .value(1600))
                .andExpect(jsonPath("$.active")
                        .value(true))
                .andExpect(jsonPath("$.alertSent")
                        .value(false));
    }

    @Test
    void shouldRejectInvalidRequest() throws Exception {

        String requestBody = """
                {
                    "hotelUrl": "",
                    "checkIn": null,
                    "checkOut": null,
                    "adults": 0,
                    "rooms": 0,
                    "targetPrice": -100
                }
                """;

        mockMvc.perform(
                        post("/api/monitors")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.hotelUrl").exists())
                .andExpect(jsonPath("$.errors.checkIn").exists())
                .andExpect(jsonPath("$.errors.checkOut").exists())
                .andExpect(jsonPath("$.errors.adults").exists())
                .andExpect(jsonPath("$.errors.rooms").exists())
                .andExpect(jsonPath("$.errors.targetPrice").exists());
    }

    @Test
    void shouldGetAllMonitors() throws Exception {

        HotelMonitor monitor = createMonitor();

        hotelMonitorRepository.save(monitor);

        mockMvc.perform(
                        get("/api/monitors")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].adults").value(2))
                .andExpect(jsonPath("$[0].rooms").value(1))
                .andExpect(jsonPath("$[0].targetPrice").value(1600));
    }

    @Test
    void shouldGetMonitorById() throws Exception {

        HotelMonitor monitor = createMonitor();

        HotelMonitor savedMonitor =
                hotelMonitorRepository.save(monitor);

        mockMvc.perform(
                        get("/api/monitors/" + savedMonitor.getId())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(savedMonitor.getId()))
                .andExpect(jsonPath("$.hotelUrl")
                        .value("https://www.oyorooms.com/192673/"))
                .andExpect(jsonPath("$.targetPrice")
                        .value(1600));
    }

    @Test
    void shouldReturn404WhenMonitorDoesNotExist() throws Exception {

        mockMvc.perform(
                        get("/api/monitors/99999")
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnEmptyPriceHistoryForNewMonitor() throws Exception {

        HotelMonitor monitor = createMonitor();

        HotelMonitor savedMonitor =
                hotelMonitorRepository.save(monitor);

        mockMvc.perform(
                        get("/api/monitors/"
                                + savedMonitor.getId()
                                + "/history")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    private HotelMonitor createMonitor() {

        HotelMonitor monitor = new HotelMonitor();

        monitor.setHotelUrl(
                "https://www.oyorooms.com/192673/"
        );

        monitor.setCheckIn(
                LocalDate.of(2026, 9, 20)
        );

        monitor.setCheckOut(
                LocalDate.of(2026, 9, 21)
        );

        monitor.setAdults(2);
        monitor.setRooms(1);
        monitor.setTargetPrice(1600.0);
        monitor.setActive(true);
        monitor.setAlertSent(false);

        return monitor;
    }
}