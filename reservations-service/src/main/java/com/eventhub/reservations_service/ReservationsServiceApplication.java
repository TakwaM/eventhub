package com.eventhub.reservations_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class ReservationsServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReservationsServiceApplication.class, args);
	}

}
