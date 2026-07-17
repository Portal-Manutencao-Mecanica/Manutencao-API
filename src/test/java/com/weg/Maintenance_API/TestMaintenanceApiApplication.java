package com.weg.Maintenance_API;

import org.springframework.boot.SpringApplication;

public class TestMaintenanceApiApplication {

	public static void main(String[] args) {
		SpringApplication.from(MaintenanceApiApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}

