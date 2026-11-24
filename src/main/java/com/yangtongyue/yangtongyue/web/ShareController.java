package com.yangtongyue.yangtongyue.web;

import java.security.Principal;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Base64;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.yangtongyue.yangtongyue.domain.Album;
import com.yangtongyue.yangtongyue.domain.ShareLink;
import com.yangtongyue.yangtongyue.repo.AlbumRepository;
import com.yangtongyue.yangtongyue.repo.ShareLinkRepository;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@RestController
@RequestMapping("/api/shares")
public class ShareController {

	/** {@code albumId == null} shares the whole timeline; {@code expiresInDays == null} never expires. */
	public record ShareRequest(Integer albumId, @Min(1) @Max(3650) Integer expiresInDays) {
	}

	public record ShareDto(Integer id, String token, Integer albumId, String albumName, Long expiresAt,
			boolean active, long createdAt) {
	}

	private static final SecureRandom RANDOM = new SecureRandom();

	private final ShareLinkRepository shares;
	private final AlbumRepository albums;

	public ShareController(ShareLinkRepository shares, AlbumRepository albums) {
		this.shares = shares;
		this.albums = albums;
	}

	@GetMapping
	public List<ShareDto> list() {
		return shares.findAllByOrderByCreatedAtDesc().stream().filter(s -> !s.isRevoked()).map(this::toDto).toList();
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ShareDto create(@Valid @RequestBody ShareRequest body, Principal principal) {
		if (body.albumId() != null && !albums.existsById(body.albumId())) {
			throw NotFound.of("album");
		}
		long now = System.currentTimeMillis();
		ShareLink s = new ShareLink();
		s.setToken(newToken());
		s.setAlbumId(body.albumId());
		s.setExpiresAt(body.expiresInDays() == null ? null : now + Duration.ofDays(body.expiresInDays()).toMillis());
		s.setCreatedBy(principal.getName());
		s.setCreatedAt(now);
		return toDto(shares.save(s));
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void revoke(@PathVariable Integer id) {
		ShareLink s = shares.findById(id).orElseThrow(() -> NotFound.of("share"));
		s.setRevoked(true);
		shares.save(s);
	}

	private ShareDto toDto(ShareLink s) {
		String albumName = s.getAlbumId() == null ? null
				: albums.findById(s.getAlbumId()).map(Album::getName).orElse(null);
		return new ShareDto(s.getId(), s.getToken(), s.getAlbumId(), albumName, s.getExpiresAt(),
				s.isActive(System.currentTimeMillis()), s.getCreatedAt());
	}

	private static String newToken() {
		byte[] bytes = new byte[18];
		RANDOM.nextBytes(bytes);
		return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
	}
}
