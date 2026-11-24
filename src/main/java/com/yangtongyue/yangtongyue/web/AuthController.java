package com.yangtongyue.yangtongyue.web;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.yangtongyue.yangtongyue.domain.AppUser;
import com.yangtongyue.yangtongyue.repo.AppUserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	public record LoginRequest(@NotBlank String username, @NotBlank String password) {
	}

	public record Me(String username, String displayName, String role) {
		static Me of(AppUser u) {
			return new Me(u.getUsername(), u.getDisplayName(), u.getRole());
		}
	}

	private final AuthenticationManager authenticationManager;
	private final SecurityContextRepository contextRepository;
	private final AppUserRepository users;

	public AuthController(AuthenticationManager authenticationManager, SecurityContextRepository contextRepository,
			AppUserRepository users) {
		this.authenticationManager = authenticationManager;
		this.contextRepository = contextRepository;
		this.users = users;
	}

	@PostMapping("/login")
	public Me login(@Valid @RequestBody LoginRequest body, HttpServletRequest request, HttpServletResponse response) {
		Authentication auth;
		try {
			auth = authenticationManager
					.authenticate(UsernamePasswordAuthenticationToken.unauthenticated(body.username(), body.password()));
		} catch (AuthenticationException e) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "用户名或密码错误");
		}
		if (request.getSession(false) != null) {
			request.changeSessionId();
		}
		SecurityContext context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(auth);
		SecurityContextHolder.setContext(context);
		contextRepository.saveContext(context, request, response);
		return me(auth);
	}

	@PostMapping("/logout")
	public void logout(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.invalidate();
		}
		SecurityContextHolder.clearContext();
	}

	@GetMapping("/me")
	public Me me(Principal principal) {
		return users.findByUsername(principal.getName()).map(Me::of)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
	}
}
