package com.inity.tickenity.domain.venue.dto;

import com.inity.tickenity.domain.venue.entity.Venue;

public record VenueResponseDto(
	Long id,
	String address,
	String name,
	int capacity,
	String description
) {
	public static VenueResponseDto toDto(Venue venue) {
		return new VenueResponseDto(
			venue.getId(),
			venue.getAddress(),
			venue.getName(),
			venue.getCapacity(),
			venue.getDescription()
		);
	}
}
