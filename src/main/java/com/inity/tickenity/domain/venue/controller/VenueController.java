package com.inity.tickenity.domain.venue.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inity.tickenity.domain.venue.dto.CreatingVenueRequestDto;
import com.inity.tickenity.domain.venue.dto.VenueResponseDto;
import com.inity.tickenity.domain.venue.service.VenueService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/concert")
public class VenueController {
	private final VenueService venueService;

	@PostMapping("/{concertId}/venues")
	public ResponseEntity<Long> createVenue(@PathVariable long concertId, @RequestBody CreatingVenueRequestDto req) {

		return ResponseEntity.ok(venueService.createVenue(concertId, req));
	}

	@GetMapping("/{concertId}/venues")
	public ResponseEntity<List<VenueResponseDto>> readVenues(@PathVariable long concertId) {
		return ResponseEntity.ok(venueService.readVenueWithConcert(concertId));
	}
}
