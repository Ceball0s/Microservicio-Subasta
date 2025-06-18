package com.Subasta;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication

	public class SubastaApplication {

		public static void main(String[] args) {
		Dotenv dotenv = Dotenv.load();
		
		// Poner las variables en System properties para que Spring Boot las lea
		System.setProperty("DB_USERNAME", dotenv.get("DB_USERNAME"));
		System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
		System.setProperty("DB_URL", dotenv.get("DB_URL"));
		
		SpringApplication.run(SubastaApplication.class, args);
	}


}
