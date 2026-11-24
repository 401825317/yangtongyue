package com.yangtongyue.yangtongyue.config;

import java.time.LocalDate;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public record AppProperties(Baby baby, Admin admin, Storage storage) {

	public record Baby(String name, LocalDate birthday) {
	}

	public record Admin(String username, String password) {
	}

	public record Storage(String type, Local local, Cos cos) {
	}

	public record Local(String dir) {
	}

	public record Cos(String secretId, String secretKey, String region, String bucket, String publicBaseUrl) {
	}
}
