package com.foodtraffic.vendor;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(com.foodtraffic.components.CustomExceptionHandler.class)
@EnableFeignClients(basePackages = { "com.foodtraffic.client" })
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	ModelMapper modelMapper() {
		return new ModelMapper();
	}
}
