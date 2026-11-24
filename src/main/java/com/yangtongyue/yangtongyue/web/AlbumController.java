package com.yangtongyue.yangtongyue.web;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.yangtongyue.yangtongyue.domain.Album;
import com.yangtongyue.yangtongyue.domain.Media;
import com.yangtongyue.yangtongyue.domain.MediaType;
import com.yangtongyue.yangtongyue.repo.AlbumRepository;
import com.yangtongyue.yangtongyue.repo.MediaRepository;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@RestController
@RequestMapping("/api/albums")
public class AlbumController {

	public record AlbumRequest(@NotBlank @Size(max = 100) String name, @Size(max = 1000) String description) {
	}

	public record AlbumDto(Integer id, String name, String description, long count, String coverUrl,
			long createdAt) {
	}

	private final AlbumRepository albumRepository;
	private final MediaRepository mediaRepository;

	public AlbumController(AlbumRepository albumRepository, MediaRepository mediaRepository) {
		this.albumRepository = albumRepository;
		this.mediaRepository = mediaRepository;
	}

	@GetMapping
	public List<AlbumDto> list() {
		return albumRepository.findAll().stream().map(this::toDto).toList();
	}

	@GetMapping("/{id}")
	public AlbumDto get(@PathVariable Integer id) {
		return toDto(albumRepository.findById(id).orElseThrow(() -> NotFound.of("album")));
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public AlbumDto create(@Valid @RequestBody AlbumRequest body) {
		Album a = new Album();
		a.setName(body.name());
		a.setDescription(body.description());
		a.setCreatedAt(System.currentTimeMillis());
		return toDto(albumRepository.save(a));
	}

	@PutMapping("/{id}")
	public AlbumDto update(@PathVariable Integer id, @Valid @RequestBody AlbumRequest body) {
		Album a = albumRepository.findById(id).orElseThrow(() -> NotFound.of("album"));
		a.setName(body.name());
		a.setDescription(body.description());
		return toDto(albumRepository.save(a));
	}

	/** Deletes the album only; its media stay on the timeline. */
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@Transactional
	public void delete(@PathVariable Integer id) {
		Album a = albumRepository.findById(id).orElseThrow(() -> NotFound.of("album"));
		mediaRepository.clearAlbum(a.getId());
		albumRepository.delete(a);
	}

	private AlbumDto toDto(Album a) {
		String cover = mediaRepository.findTimelineByAlbum(a.getId()).stream()
				.filter(m -> m.getMediaType() == MediaType.IMAGE)
				.map(Media::getUploadpath).findFirst().orElse(null);
		return new AlbumDto(a.getId(), a.getName(), a.getDescription(), mediaRepository.countByAlbumId(a.getId()),
				cover, a.getCreatedAt());
	}
}
