package com.inity.tickenity.domain.concert.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.inity.tickenity.domain.concert.dto.ConcertResponseDto;
import com.inity.tickenity.domain.concert.dto.ConcertWithGenreResponseDto;
import com.inity.tickenity.domain.concert.dto.RequestConcert;
import com.inity.tickenity.domain.concert.enums.Genre;
import com.inity.tickenity.domain.concert.service.ConcertService;
import com.inity.tickenity.global.response.BaseResponse;
import com.inity.tickenity.global.response.ResultCode;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/concerts")
@RequiredArgsConstructor
public class ConcertController {
	private final ConcertService concertService;

	@PostMapping
	public BaseResponse<Long> postConcert(@Valid @RequestBody RequestConcert req) {
		Long concertId = concertService.postConcert(req);
		return BaseResponse.success(concertId, ResultCode.CREATED);
	}

	@GetMapping
	public BaseResponse<List<ConcertWithGenreResponseDto>> readConcertsByGenre(@RequestParam Genre genre) {
		return BaseResponse.success(concertService.readConcertsByGenre(genre), ResultCode.OK);
	}

	@GetMapping("/{concertId}")
	public BaseResponse<ConcertResponseDto> readConcert(@PathVariable Long concertId) {
		return BaseResponse.success(concertService.readConcert(concertId), ResultCode.OK);
	}
}
