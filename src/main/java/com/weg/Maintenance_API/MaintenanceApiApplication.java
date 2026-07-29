package com.weg.Maintenance_API;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class MaintenanceApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(MaintenanceApiApplication.class, args);
    }
}