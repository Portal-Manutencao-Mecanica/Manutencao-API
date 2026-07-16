package com.weg.Manutencao_API;

import org.springframework.boot.SpringApplication;

public class TestManutencaoApiApplication {

	public static void main(String[] args) {
		SpringApplication.from(ManutencaoApiApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
