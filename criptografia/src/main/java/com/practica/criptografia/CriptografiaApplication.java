package com.practica.criptografia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CriptografiaApplication {

	public static void main(String[] args) {
		SpringApplication.run(CriptografiaApplication.class, args);
	}

}
