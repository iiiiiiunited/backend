package com.inity.tickenity.domain.concert.dto;

import java.util.List;

import com.inity.tickenity.domain.concert.entity.Concert;
import com.inity.tickenity.domain.concert.enums.Genre;
import com.inity.tickenity.domain.venue.entity.Venue;

import lombok.Builder;

@Builder
public record ConcertResponseDto(
	long id,
	String title,
	String ageRating,
	int duration,
	Genre genre,
	String description,
	String postUrl,
	List<Venue> venues
) {
	public static ConcertResponseDto toDto(Concert concert, List<Venue> venues) {
		return ConcertResponseDto.builder()
			.id(concert.getId())
			.title(concert.getTitle())
			.ageRating(concert.getAgeRating())
			.duration(concert.getDuration())
			.genre(concert.getGenre())
			.description(concert.getDescription())
			.postUrl(concert.getPostUrl())
			.venues(venues)
			.build();
	}
}
