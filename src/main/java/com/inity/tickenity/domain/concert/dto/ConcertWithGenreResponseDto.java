package com.inity.tickenity.domain.concert.dto;

import com.inity.tickenity.domain.concert.entity.Concert;
import com.inity.tickenity.domain.concert.enums.Genre;

import lombok.Builder;

@Builder
public record ConcertWithGenreResponseDto(
	long id,
	String title,
	String ageRating,
	int duration,
	Genre genre,
	String postUrl
) {
	public static ConcertWithGenreResponseDto toDto(Concert concert) {
		return ConcertWithGenreResponseDto.builder()
			.id(concert.getId())
			.title(concert.getTitle())
			.ageRating(concert.getAgeRating())
			.duration(concert.getDuration())
			.genre(concert.getGenre())
			.postUrl(concert.getPostUrl())
			.build();
	}
}
