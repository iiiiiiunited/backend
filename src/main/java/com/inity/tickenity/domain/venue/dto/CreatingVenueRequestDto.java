package com.inity.tickenity.domain.venue.dto;

import com.inity.tickenity.domain.venue.entity.Venue;
import jakarta.validation.constraints.*;

public record CreatingVenueRequestDto(
	@NotBlank(message = "주소는 필수입니다.")
	String address,

	@NotBlank(message = "이름은 필수입니다.")
	String name,

	@Positive(message = "수용 인원은 0보다 커야 합니다.")
	int capacity,

	@NotBlank(message = "설명은 필수입니다.")
	String description
) {
	public Venue fromDto() {
		return new Venue(
			address,
			name,
			capacity,
			description
		);
	}
}
