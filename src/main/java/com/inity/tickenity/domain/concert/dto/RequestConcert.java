package com.inity.tickenity.domain.concert.dto;

import com.inity.tickenity.domain.concert.entity.Concert;
import com.inity.tickenity.domain.concert.enums.Genre;
import jakarta.validation.constraints.*;

import java.util.List;

public record RequestConcert(
	@NotBlank(message = "제목은 필수입니다.")
	String title,

	@NotBlank(message = "관람 등급은 필수입니다.")
	String ageRating,

	@Positive(message = "공연 시간은 양수여야 합니다.")
	int duration,

	@NotNull(message = "장르는 필수입니다.")
	Genre genre,

	@NotBlank(message = "설명은 필수입니다.")
	String description,

	@NotBlank(message = "포스터 URL은 필수입니다.")
	String postUrl,

	@NotEmpty(message = "공연장 ID는 하나 이상 필요합니다.")
	List<@NotNull(message = "공연장 ID는 null일 수 없습니다.") Long> venueIds
) {
	public Concert fromDto() {
		return new Concert(
			title,
			ageRating,
			duration,
			genre,
			description,
			postUrl
		);
	}
}