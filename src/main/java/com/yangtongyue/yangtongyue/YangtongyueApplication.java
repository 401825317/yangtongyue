package com.yangtongyue.yangtongyue;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class YangtongyueApplication {

	public static void main(String[] args) {
		SpringApplication.run(YangtongyueApplication.class, args);
	}
}
