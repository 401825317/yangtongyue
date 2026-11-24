package com.yangtongyue.yangtongyue.web;

import java.security.Principal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.yangtongyue.yangtongyue.domain.AppUser;
import com.yangtongyue.yangtongyue.repo.AppUserRepository;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Admin-only management of family member accounts. */
@RestController
@RequestMapping("/api/users")
public class UserController {

	public record UserRequest(@NotBlank @Size(max = 64) String username, @Size(max = 64) String displayName,
			@NotBlank @Size(min = 6, max = 100) String password,
			@Pattern(regexp = "ADMIN|MEMBER") String role) {
	}

	public record UserDto(Integer id, String username, String displayName, String role) {
		static UserDto of(AppUser u) {
			return new UserDto(u.getId(), u.getUsername(), u.getDisplayName(), u.getRole());
		}
	}

	private final AppUserRepository users;
	private final PasswordEncoder encoder;

	public UserController(AppUserRepository users, PasswordEncoder encoder) {
		this.users = users;
		this.encoder = encoder;
	}

	@GetMapping
	public List<UserDto> list() {
		return users.findAll().stream().map(UserDto::of).toList();
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public UserDto create(@Valid @RequestBody UserRequest body) {
		if (users.existsByUsername(body.username())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "用户名已存在");
		}
		AppUser u = new AppUser();
		u.setUsername(body.username());
		u.setDisplayName(body.displayName() == null || body.displayName().isBlank() ? body.username()
				: body.displayName());
		u.setRole(body.role() == null ? AppUser.ROLE_MEMBER : body.role());
		u.setPasswordHash(encoder.encode(body.password()));
		return UserDto.of(users.save(u));
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Integer id, Principal principal) {
		AppUser u = users.findById(id).orElseThrow(() -> NotFound.of("user"));
		if (u.getUsername().equals(principal.getName())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "不能删除自己");
		}
		users.delete(u);
	}
}
