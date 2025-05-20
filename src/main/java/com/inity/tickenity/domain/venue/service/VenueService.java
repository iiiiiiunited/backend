package com.inity.tickenity.domain.venue.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.inity.tickenity.domain.concert.entity.Concert;
import com.inity.tickenity.domain.concert.repository.ConcertRepository;
import com.inity.tickenity.domain.concertvenue.ConcertVenueRepository;
import com.inity.tickenity.domain.venue.dto.CreatingVenueRequestDto;
import com.inity.tickenity.domain.venue.dto.VenueResponseDto;
import com.inity.tickenity.domain.venue.entity.Venue;
import com.inity.tickenity.domain.venue.repository.VenueRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VenueService {
	private final VenueRepository venueRepository;
	// private final ConcertRepository concertRepository;
	private final ConcertVenueRepository concertVenueRepository;

	// public long createVenue(long concertId, CreatingVenueRequestDto req) {
	// 	Concert concert = ConcertVenueRepository.findByConcertId(concertId);
	// 	return venueRepository.save(req.fromDto(concert)).getId();
	// }

	public List<VenueResponseDto> readVenueWithConcert(long concertId) {
		concertVenueRepository.findById(concertId).orElseThrow();
		List<Venue> venues = venueRepository.findByConcert(concert);
		List<VenueResponseDto> dtos = new ArrayList<>();
		for(Venue venue : venues) {
			dtos.add(VenueResponseDto.toDto(venue));
		}
		return dtos;
	}
}
