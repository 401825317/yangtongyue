package com.yangtongyue.yangtongyue.web;

import java.time.LocalDate;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yangtongyue.yangtongyue.config.AppProperties;
import com.yangtongyue.yangtongyue.domain.Album;
import com.yangtongyue.yangtongyue.domain.ShareLink;
import com.yangtongyue.yangtongyue.repo.AlbumRepository;
import com.yangtongyue.yangtongyue.repo.ShareLinkRepository;
import com.yangtongyue.yangtongyue.service.MediaService;

/** Unauthenticated, read-only view behind a share token. */
@RestController
@RequestMapping("/api/public/shares")
public class PublicShareController {

	public record SharedView(String babyName, LocalDate birthday, String albumName, List<MediaDto> media) {
	}

	private final ShareLinkRepository shares;
	private final AlbumRepository albums;
	private final MediaService mediaService;
	private final AppProperties props;

	public PublicShareController(ShareLinkRepository shares, AlbumRepository albums, MediaService mediaService,
			AppProperties props) {
		this.shares = shares;
		this.albums = albums;
		this.mediaService = mediaService;
		this.props = props;
	}

	@GetMapping("/{token}")
	public SharedView view(@PathVariable String token) {
		ShareLink s = shares.findByToken(token).filter(l -> l.isActive(System.currentTimeMillis()))
				.orElseThrow(() -> NotFound.of("share"));
		String albumName = null;
		if (s.getAlbumId() != null) {
			albumName = albums.findById(s.getAlbumId()).map(Album::getName).orElseThrow(() -> NotFound.of("share"));
		}
		List<MediaDto> media = mediaService.timeline(s.getAlbumId()).stream().map(MediaDto::of).toList();
		return new SharedView(props.baby().name(), props.baby().birthday(), albumName, media);
	}
}
