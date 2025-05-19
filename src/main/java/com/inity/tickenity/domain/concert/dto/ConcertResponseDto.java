package com.inity.tickenity.domain.concert.dto;

import com.inity.tickenity.domain.concert.entity.Concert;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class ConcertResponseDto {
	private final long id;
	private final String title;
	private final String ageRating;
	private final int duration;
	private final String genre;
	private final String description;
	private final String postUrl;

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
