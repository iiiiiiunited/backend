package com.inity.tickenity.domain.concert.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inity.tickenity.domain.concert.dto.ConcertResponseDto;
import com.inity.tickenity.domain.concert.dto.ConcertWithGenreResponseDto;
import com.inity.tickenity.domain.concert.dto.RequestConcert;
import com.inity.tickenity.domain.concert.entity.Concert;
import com.inity.tickenity.domain.concert.enums.Genre;
import com.inity.tickenity.domain.concert.repository.ConcertRepository;
import com.inity.tickenity.domain.concertvenue.ConcertVenue;
import com.inity.tickenity.domain.concertvenue.ConcertVenueRepository;
import com.inity.tickenity.domain.venue.entity.Venue;
import com.inity.tickenity.domain.venue.repository.VenueRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConcertService {
	private final ConcertRepository concertRepository;
	private final VenueRepository venueRepository;
	private final ConcertVenueRepository concertVenueRepository;

	@Transactional
	public Long postConcert(RequestConcert req) {

		Concert concert = req.fromDto();
		concertRepository.save(concert);

		List<Venue> venues = venueRepository.findAllById(req.venueIds());

		if(isDuplicated(req.venueIds())) {
			throw new IllegalArgumentException("중복된 venue ID가 포함되어 있습니다.");
		}

		if (isExistingVenue(venues.size(), req.venueIds())) {
			throw new IllegalArgumentException("요청한 venue 중 존재하지 않는 ID가 있습니다.");
		}

		for (Venue venue : venues) {
			ConcertVenue concertVenue = new ConcertVenue(concert, venue);
			concertVenueRepository.save(concertVenue);
		}

		return concert.getId();
	}

	@Transactional(readOnly = true)
	public List<ConcertWithGenreResponseDto> readConcertsByGenre(Genre genre) {
		List<Concert> concerts = concertRepository.findAllByGenre(genre);

		return concerts.stream()
			.map(ConcertWithGenreResponseDto::toDto)
			.toList();
	}

	@Transactional(readOnly = true)
	public ConcertResponseDto readConcert(long id) {
		List<ConcertVenue> cvs = concertRepository.findAllByConcertIdWithVenue(id);
		List<Venue> venues = cvs.stream()
			.map(ConcertVenue::getVenue)
			.toList();
		Concert concert = concertRepository.findById(id).orElseThrow();
		return ConcertResponseDto.toDto(concert, venues);
	}

	private boolean isDuplicated(List<Long> ids) {
		long distinctCount = ids.stream().distinct().count();
		return ids.size() != distinctCount;
	}

	private boolean isExistingVenue(int venuesSize, List<Long> ids) {
		return venuesSize != ids.size();
	}
}
