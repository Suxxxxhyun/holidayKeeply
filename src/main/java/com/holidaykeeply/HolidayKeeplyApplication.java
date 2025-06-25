package com.holidaykeeply;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class HolidayKeeplyApplication {

	public static void main(String[] args) {
		SpringApplication.run(HolidayKeeplyApplication.class, args);
	}

}
