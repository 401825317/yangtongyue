package com.yangtongyue.yangtongyue.web;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

final class NotFound {

	private NotFound() {
	}

	static ResponseStatusException of(String what) {
		return new ResponseStatusException(HttpStatus.NOT_FOUND, what + " not found");
	}
}
