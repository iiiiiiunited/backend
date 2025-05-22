package com.inity.tickenity.domain.search.service;

import com.inity.tickenity.domain.common.dto.PageResponseDto;
import com.inity.tickenity.domain.concert.entity.Concert;
import com.inity.tickenity.domain.concert.repository.ConcertRepository;
import com.inity.tickenity.domain.search.dto.response.SearchConcertResultResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class SearchService {

    private final ConcertRepository concertRepository;

    public PageResponseDto<SearchConcertResultResponse> findByKeyword(String keyword, Pageable pageable) {

        Page<Concert> pages = concertRepository.findByTitleContaining(keyword, pageable);
        return PageResponseDto.toDto(
                pages.map(SearchConcertResultResponse::fromEntity)
        );
    }

    public PageResponseDto<SearchConcertResultResponse> findByKeywordWithCache(String keyword, Pageable pageable) {

        Page<Concert> pages = concertRepository.findByTitleContaining(keyword, pageable);
        return PageResponseDto.toDto(
                pages.map(SearchConcertResultResponse::fromEntity)
        );
    }

}
