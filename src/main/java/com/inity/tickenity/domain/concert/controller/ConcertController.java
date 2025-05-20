package com.inity.tickenity.domain.concert.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.inity.tickenity.domain.concert.dto.ConcertResponseDto;
import com.inity.tickenity.domain.concert.dto.ConcertWithGenreResponseDto;
import com.inity.tickenity.domain.concert.dto.RequestConcert;
import com.inity.tickenity.domain.concert.enums.Genre;
import com.inity.tickenity.domain.concert.service.ConcertService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/concerts")
@RequiredArgsConstructor
public class ConcertController {
	private final ConcertService concertService;

	@PostMapping
	public ResponseEntity<Long> postConcert(@Valid @RequestBody RequestConcert req) {
		Long concertId = concertService.postConcert(req);
		return ResponseEntity.ok(concertId);
	}

	@GetMapping
	public ResponseEntity<List<ConcertWithGenreResponseDto>> readConcertsByGenre(@RequestParam Genre genre) {
		return ResponseEntity.ok(concertService.readConcertsByGenre(genre));
	}

	@GetMapping("/{concertId}")
	public ResponseEntity<ConcertResponseDto> readConcert(@PathVariable Long concertId) {
		return ResponseEntity.ok(concertService.readConcert(concertId));
	}
}
