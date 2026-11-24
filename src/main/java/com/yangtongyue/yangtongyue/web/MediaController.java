package com.yangtongyue.yangtongyue.web;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.yangtongyue.yangtongyue.domain.Media;
import com.yangtongyue.yangtongyue.repo.AlbumRepository;
import com.yangtongyue.yangtongyue.repo.MediaRepository;
import com.yangtongyue.yangtongyue.service.MediaService;

import jakarta.validation.constraints.Size;

@RestController
@RequestMapping("/api/media")
public class MediaController {

	/** Fields to change; {@code clearAlbum=true} removes the media from its album. */
	public record MediaUpdate(@Size(max = 1000) String caption, Integer albumId, Boolean clearAlbum, Long takenAt) {
	}

	private final MediaService mediaService;
	private final MediaRepository mediaRepository;
	private final AlbumRepository albumRepository;

	public MediaController(MediaService mediaService, MediaRepository mediaRepository,
			AlbumRepository albumRepository) {
		this.mediaService = mediaService;
		this.mediaRepository = mediaRepository;
		this.albumRepository = albumRepository;
	}

	@GetMapping
	public List<MediaDto> list(@RequestParam(required = false) Integer albumId) {
		return mediaService.timeline(albumId).stream().map(MediaDto::of).toList();
	}

	@PostMapping(consumes = "multipart/form-data")
	@ResponseStatus(HttpStatus.CREATED)
	public MediaDto upload(@RequestParam MultipartFile file, @RequestParam(required = false) Integer albumId,
			@RequestParam(required = false) Long lastModified, @RequestParam(required = false) String caption)
			throws IOException {
		if (file.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "文件为空");
		}
		requireAlbum(albumId);
		return MediaDto.of(mediaService.upload(file, albumId, lastModified, caption));
	}

	@PatchMapping("/{id}")
	public MediaDto update(@PathVariable Integer id, @RequestBody MediaUpdate body) {
		Media m = mediaRepository.findById(id).orElseThrow(() -> NotFound.of("media"));
		if (body.caption() != null) {
			m.setCaption(body.caption().isBlank() ? null : body.caption());
		}
		if (Boolean.TRUE.equals(body.clearAlbum())) {
			m.setAlbumId(null);
		} else if (body.albumId() != null) {
			requireAlbum(body.albumId());
			m.setAlbumId(body.albumId());
		}
		if (body.takenAt() != null) {
			m.setTakenAt(body.takenAt());
		}
		return MediaDto.of(mediaRepository.save(m));
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Integer id) {
		mediaService.delete(mediaRepository.findById(id).orElseThrow(() -> NotFound.of("media")));
	}

	private void requireAlbum(Integer albumId) {
		if (albumId != null && !albumRepository.existsById(albumId)) {
			throw NotFound.of("album");
		}
	}
}
