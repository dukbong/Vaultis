package com.vaultis.vaultis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class VaultisApplication {

	public static void main(String[] args) {
		SpringApplication.run(VaultisApplication.class, args);
	}

}
