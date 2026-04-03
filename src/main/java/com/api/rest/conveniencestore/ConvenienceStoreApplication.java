package com.api.rest.conveniencestore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ConvenienceStoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConvenienceStoreApplication.class, args);
		System.out.println("Project started");
	}
}
