package com.hcteol.jwt.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
		System.setProperty("DB_HOST", System.getenv("DB_HOST"));
		System.setProperty("DB_PORT", System.getenv("DB_PORT"));
		System.setProperty("DB_NAME", System.getenv("DB_NAME"));
		System.setProperty("DB_USER", System.getenv("DB_USER"));
		System.setProperty("DB_PASSWORD", System.getenv("DB_PASSWORD"));
		System.setProperty("JWT_SECRET", System.getenv("JWT_SECRET"));
		System.setProperty("SERVER_PORT", System.getenv("SERVER_PORT"));
		System.out.println("DB_HOST: " + System.getenv("DB_HOST"));
		System.out.println("DB_PORT: " + System.getenv("DB_PORT"));
		System.out.println("DB_NAME: " + System.getenv("DB_NAME"));
		System.out.println("DB_USER: " + System.getenv("DB_USER"));
		System.out.println("DB_PASSWORD: " + System.getenv("DB_PASSWORD"));
		System.out.println("JWT_SECRET: " + System.getenv("JWT_SECRET"));
		System.out.println("SERVER_PORT: " + System.getenv("SERVER_PORT"));
		SpringApplication.run(BackendApplication.class, args);
	}

}
