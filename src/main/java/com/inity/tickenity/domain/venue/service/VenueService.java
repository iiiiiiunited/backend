package com.inity.tickenity.domain.venue.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inity.tickenity.domain.concert.entity.Concert;
import com.inity.tickenity.domain.concert.repository.ConcertRepository;
import com.inity.tickenity.domain.concertvenue.ConcertVenue;
import com.inity.tickenity.domain.concertvenue.ConcertVenueRepository;
import com.inity.tickenity.domain.venue.dto.CreateVenueRequestDto;
import com.inity.tickenity.domain.venue.dto.CreatingVenueRequestDto;
import com.inity.tickenity.domain.venue.dto.VenueResponseDto;
import com.inity.tickenity.domain.venue.entity.Venue;
import com.inity.tickenity.domain.venue.repository.VenueRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VenueService {
	private final VenueRepository venueRepository;
	private final ConcertRepository concertRepository;

	@Transactional
	public long createVenue(CreateVenueRequestDto req) {
		return venueRepository.save(req.fromDto()).getId();
	}

	@Transactional(readOnly = true)
	public List<VenueResponseDto> readVenueWithConcert(long concertId) {
		Concert concert = concertRepository.findById(concertId).orElseThrow();
		List<Venue> venues = venueRepository.findAllByConcert(concert);
		return venues.stream()
			.map(VenueResponseDto::toDto)
			.toList();
	}
}
