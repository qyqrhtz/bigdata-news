package com.example.bigdata_news;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BigdataNewsApplication {

	public static void main(String[] args) {
		SpringApplication.run(BigdataNewsApplication.class, args);
	}

}
