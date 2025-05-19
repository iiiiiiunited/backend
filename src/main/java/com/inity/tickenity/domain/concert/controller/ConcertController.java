package com.inity.tickenity.domain.concert.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.inity.tickenity.domain.concert.dto.ConcertByGenreResponseDto;
import com.inity.tickenity.domain.concert.dto.RequestConcert;
import com.inity.tickenity.domain.concert.service.ConcertService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/concert")
@RequiredArgsConstructor
public class ConcertController {
	private final ConcertService concertService;

	@PostMapping
	public ResponseEntity<Long> postConcert(@RequestBody RequestConcert req) {
		Long concertId = concertService.postConcert(req);
		return ResponseEntity.ok(concertId);
	}

	@GetMapping
	public ResponseEntity<List<ConcertByGenreResponseDto>> readConcertsByGenre(@RequestParam String genre) {
		return ResponseEntity.ok(concertService.readConcertsByGenre(genre));
	}
}
