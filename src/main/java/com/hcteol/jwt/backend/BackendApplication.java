package com.hcteol.jwt.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackendApplication {

    public static void main(String[] args) {
        setSystemPropertyFromEnv("DB_HOST");
        setSystemPropertyFromEnv("DB_PORT");
        setSystemPropertyFromEnv("DB_NAME");
        setSystemPropertyFromEnv("DB_USER");
        setSystemPropertyFromEnv("DB_PASSWORD");
        setSystemPropertyFromEnv("JWT_SECRET");
        setSystemPropertyFromEnv("SERVER_PORT");
        System.out.println("DB_HOST: " + System.getenv("DB_HOST"));
        System.out.println("DB_PORT: " + System.getenv("DB_PORT"));
        System.out.println("DB_NAME: " + System.getenv("DB_NAME"));
        System.out.println("DB_USER: " + System.getenv("DB_USER"));
        System.out.println("DB_PASSWORD: " + System.getenv("DB_PASSWORD"));
        System.out.println("JWT_SECRET: " + System.getenv("JWT_SECRET"));
        System.out.println("SERVER_PORT: " + System.getenv("SERVER_PORT"));
        SpringApplication.run(BackendApplication.class, args);
    }

    private static void setSystemPropertyFromEnv(String key) {
        String value = System.getenv(key);
        if (value != null && !value.isBlank()) {
            System.setProperty(key, value);
        }
    }

}
