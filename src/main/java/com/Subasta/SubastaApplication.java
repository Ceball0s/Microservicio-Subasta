package com.Subasta;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
<<<<<<< Updated upstream
<<<<<<< Updated upstream

@SpringBootApplication
public class SubastaApplication {

	public static void main(String[] args) {
		SpringApplication.run(SubastaApplication.class, args);
	}

=======
=======
>>>>>>> Stashed changes
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
public class SubastaApplication {

    public static void main(String[] args) {
        SpringApplication.run(SubastaApplication.class, args);
    }
<<<<<<< Updated upstream
>>>>>>> Stashed changes
=======
>>>>>>> Stashed changes
}
