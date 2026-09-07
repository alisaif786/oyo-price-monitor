package com.example.oyo_price_monitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class OyoPriceMonitorApplication {

	public static void main(String[] args) {
		SpringApplication.run(OyoPriceMonitorApplication.class, args);
	}
}