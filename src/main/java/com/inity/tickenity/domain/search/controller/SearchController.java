package com.inity.tickenity.domain.search.controller;

import com.inity.tickenity.domain.common.dto.PageResponseDto;
import com.inity.tickenity.domain.search.dto.response.SearchConcertResultResponse;
import com.inity.tickenity.domain.search.service.SearchService;
import com.inity.tickenity.global.response.BaseResponse;
import com.inity.tickenity.global.response.ResultCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @GetMapping("/v1")
    public BaseResponse<PageResponseDto<SearchConcertResultResponse>> getConcertByKeywordV1(
            @RequestParam String keyword,
            @PageableDefault Pageable pageable
        ) {
        PageResponseDto<SearchConcertResultResponse> result = searchService.findByKeyword(keyword, pageable);

        return BaseResponse.success(result, ResultCode.OK);
    }

    @GetMapping("/v2")
    public BaseResponse<PageResponseDto<SearchConcertResultResponse>> getConcertByKeywordV2(
            @RequestParam String keyword,
            @PageableDefault Pageable pageable
    ) {
        PageResponseDto<SearchConcertResultResponse> result = searchService.findByKeywordWithLocalCache(keyword, pageable);

        return BaseResponse.success(result, ResultCode.OK);
    }

    @GetMapping("/v3")
    public BaseResponse<PageResponseDto<SearchConcertResultResponse>> getConcertByKeywordV3(
            @RequestParam String keyword,
            @PageableDefault Pageable pageable
    ) {
        PageResponseDto<SearchConcertResultResponse> result = searchService.findByKeywordWithRedis(keyword, pageable);

        return BaseResponse.success(result, ResultCode.OK);
    }

}
