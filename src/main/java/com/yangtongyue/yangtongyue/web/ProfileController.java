package com.yangtongyue.yangtongyue.web;

import java.time.LocalDate;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yangtongyue.yangtongyue.config.AppProperties;

@RestController
public class ProfileController {

	public record Profile(String babyName, LocalDate birthday) {
	}

	private final AppProperties props;

	public ProfileController(AppProperties props) {
		this.props = props;
	}

	@GetMapping("/api/profile")
	public Profile profile() {
		return new Profile(props.baby().name(), props.baby().birthday());
	}
}
