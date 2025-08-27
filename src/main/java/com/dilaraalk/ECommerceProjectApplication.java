package com.dilaraalk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

@EntityScan(basePackages = {"com.dilaraalk"})
@ComponentScan(basePackages = {"com.dilaraalk"})
@EnableJpaRepositories(basePackages = "com.dilaraalk") 
@SpringBootApplication
@EnableAsync
public class ECommerceProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(ECommerceProjectApplication.class, args);
	}

}
