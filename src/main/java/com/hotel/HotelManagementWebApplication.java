package com.hotel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point of the Grand Stay Hotel Management System.
 * Starts the Spring Boot application and its embedded web server.
 */
@SpringBootApplication
public class HotelManagementWebApplication {

    /**
     * Starts the application.
     *
     * @param args command-line arguments supplied at startup
     */
    public static void main(String[] args) {
        SpringApplication.run(HotelManagementWebApplication.class, args);
    }
}
