package com.inity.tickenity.domain.venue.dto;

import com.inity.tickenity.domain.venue.entity.Venue;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class VenueResponseDto {
	private final Long id;

	private final String address;
	private final String name;
	private final int capacity;
	private final String description;

	public static VenueResponseDto toDto(Venue venue) {
		return VenueResponseDto.builder()
			.id(venue.getId())
			.address(venue.getAddress())
			.name(venue.getName())
			.capacity(venue.getCapacity())
			.description(venue.getDescription())
			.build();
	}
}
