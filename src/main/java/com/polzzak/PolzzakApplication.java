package com.polzzak;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class PolzzakApplication {

	public static void main(String[] args) {
		SpringApplication.run(PolzzakApplication.class, args);
	}

}
