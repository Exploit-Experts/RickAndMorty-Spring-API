package com.rickmorty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RickMortyApplication {

	public static void main(String[] args) {
		SpringApplication.run(RickMortyApplication.class, args);
	}

}
