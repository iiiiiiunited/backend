package com.inity.tickenity.domain.venue.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.inity.tickenity.domain.venue.dto.CreateVenueRequestDto;
import com.inity.tickenity.domain.venue.dto.VenueResponseDto;
import com.inity.tickenity.domain.venue.service.VenueService;
import com.inity.tickenity.global.response.BaseResponse;
import com.inity.tickenity.global.response.ResultCode;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class VenueController {
	private final VenueService venueService;

	@PostMapping("/venues")
	public BaseResponse<Long> createVenue(@Valid @RequestBody CreateVenueRequestDto req) {
		return BaseResponse.success(venueService.createVenue(req), ResultCode.CREATED);
	}

	@GetMapping("/concert/{concertId}/venues")
	public BaseResponse<List<VenueResponseDto>> readVenues(@PathVariable long concertId) {
		return BaseResponse.success(venueService.readVenueWithConcert(concertId), ResultCode.OK);
	}
}
