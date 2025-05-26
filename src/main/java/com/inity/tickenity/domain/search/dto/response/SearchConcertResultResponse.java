package com.inity.tickenity.domain.search.dto.response;

import com.inity.tickenity.domain.concert.entity.Concert;
import com.inity.tickenity.domain.concert.enums.Genre;

public record SearchConcertResultResponse(
        Long id,
        String title,
        int duration,
        Genre genre,
        String description,
        String postUrl
) {

    public static SearchConcertResultResponse fromEntity(Concert concert) {
        return new SearchConcertResultResponse(
                concert.getId(),
                concert.getTitle(),
                concert.getDuration(),
                concert.getGenre(),
                concert.getDescription(),
                concert.getPostUrl()
        );
    }

}
