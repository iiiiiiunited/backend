package com.inity.tickenity.domain.concert.dto;

import com.inity.tickenity.domain.concert.entity.Concert;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class RequestConcert {
	private final String title;
	private final String ageRating;
	private final int duration;
	private final String genre;
	private final String description;
	private final String postUrl;

	public Concert fromDto(RequestConcert req) {
		return new Concert(req.title, req.ageRating, req.duration, req.genre, req.description, req.postUrl);
	}
}
