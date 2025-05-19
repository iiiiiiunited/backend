package com.inity.tickenity.domain.concert.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.inity.tickenity.domain.concert.dto.ConcertResponseDto;
import com.inity.tickenity.domain.concert.dto.RequestConcert;
import com.inity.tickenity.domain.concert.entity.Concert;
import com.inity.tickenity.domain.concert.repository.ConcertRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConcertService {
	private final ConcertRepository concertRepository;

	public Long postConcert(RequestConcert req) {
		return concertRepository.save(req.fromDto(req)).getId();
	}

	public List<ConcertResponseDto> readConcertsByGenre(String genre) {
		List<Concert> concerts = concertRepository.findByGenre(genre);
		List<ConcertResponseDto> responseDtos = new ArrayList<>();
		for(Concert concert : concerts) {
			responseDtos.add(ConcertResponseDto.toDto(concert));
		}
		return responseDtos;
	}

	public ConcertResponseDto readConcert(long id) {
		Concert concert = concertRepository.findById(id).orElseThrow();
		return ConcertResponseDto.toDto(concert);
	}
}
