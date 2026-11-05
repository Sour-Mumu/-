package com.bikesystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "com.bikesystem.model")
@EnableJpaRepositories(basePackages = "com.bikesystem.repository")
public class BikeSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(BikeSystemApplication.class, args);
    }
}
