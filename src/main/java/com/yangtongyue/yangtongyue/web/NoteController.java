package com.yangtongyue.yangtongyue.web;

import java.security.Principal;
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

import com.yangtongyue.yangtongyue.domain.Note;
import com.yangtongyue.yangtongyue.repo.NoteRepository;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

	public record NoteRequest(@NotBlank @Size(max = 32) String author, @NotBlank @Size(max = 10000) String content) {
	}

	public record NoteDto(Integer id, String author, String content, String createdBy, long createdAt) {
		static NoteDto of(Note n) {
			return new NoteDto(n.getId(), n.getAuthor(), n.getContent(), n.getCreatedBy(), n.getCreatedAt());
		}
	}

	private final NoteRepository notes;

	public NoteController(NoteRepository notes) {
		this.notes = notes;
	}

	@GetMapping
	public List<NoteDto> list() {
		return notes.findAllByOrderByCreatedAtDesc().stream().map(NoteDto::of).toList();
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public NoteDto create(@Valid @RequestBody NoteRequest body, Principal principal) {
		Note n = new Note();
		n.setAuthor(body.author());
		n.setContent(body.content());
		n.setCreatedBy(principal.getName());
		n.setCreatedAt(System.currentTimeMillis());
		return NoteDto.of(notes.save(n));
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Integer id) {
		notes.delete(notes.findById(id).orElseThrow(() -> NotFound.of("note")));
	}
}
