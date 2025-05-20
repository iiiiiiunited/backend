package com.inity.tickenity.domain.concert.dto;

import com.inity.tickenity.domain.concert.entity.Concert;
import com.inity.tickenity.domain.concert.enums.Genre;

import lombok.Builder;

@Builder
public record ConcertResponseDto(
	long id,
	String title,
	String ageRating,
	int duration,
	Genre genre,
	String description,
	String postUrl
) {
	public static ConcertResponseDto toDto(Concert concert) {
		return ConcertResponseDto.builder()
			.id(concert.getId())
			.title(concert.getTitle())
			.ageRating(concert.getAgeRating())
			.duration(concert.getDuration())
			.genre(concert.getGenre())
			.description(concert.getDescription())
			.postUrl(concert.getPostUrl())
			.build();
	}
}
