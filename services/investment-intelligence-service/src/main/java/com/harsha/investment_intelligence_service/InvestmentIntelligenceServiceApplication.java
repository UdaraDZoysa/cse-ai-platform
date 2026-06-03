package com.harsha.investment_intelligence_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class InvestmentIntelligenceServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InvestmentIntelligenceServiceApplication.class, args);
	}

}
