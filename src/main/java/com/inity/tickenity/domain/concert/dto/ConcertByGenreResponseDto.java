package com.inity.tickenity.domain.concert.dto;

import org.springframework.data.domain.Page;

import com.inity.tickenity.domain.common.dto.PageResponseDto;
import com.inity.tickenity.domain.concert.entity.Concert;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class ConcertByGenreResponseDto {
	private final long id;
	private final String title;
	private final String ageRating;
	private final int duration;
	private final String description;
	private final String postUrl;

	public static ConcertByGenreResponseDto toDto(Concert concert) {
		return ConcertByGenreResponseDto.builder()
			.id(concert.getId())
			.title(concert.getTitle())
			.ageRating(concert.getAgeRating())
			.duration(concert.getDuration())
			.description(concert.getDescription())
			.postUrl(concert.getPostUrl())
			.build();
	}
}
