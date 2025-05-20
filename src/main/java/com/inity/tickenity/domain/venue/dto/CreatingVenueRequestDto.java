package com.inity.tickenity.domain.venue.dto;

import com.inity.tickenity.domain.concert.entity.Concert;
import com.inity.tickenity.domain.venue.entity.Venue;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CreatingVenueRequestDto {
	private final String address;
	private final String name;
	private final int capacity;
	private final String description;

	public Venue fromDto(Concert concert) {
		return new Venue(
			address,
			name,
			capacity,
			description
		);
	}
}
