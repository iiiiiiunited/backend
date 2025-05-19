package com.inity.tickenity.domain.reservation.controller;

import com.inity.tickenity.domain.common.dto.PageResponseDto;
import com.inity.tickenity.domain.reservation.dto.reqeust.ReservationCreateRequestDto;
import com.inity.tickenity.domain.reservation.dto.response.MyReservationResponse;
import com.inity.tickenity.domain.reservation.dto.response.ReservationDetailResponseDto;
import com.inity.tickenity.domain.reservation.dto.response.ReservationIdResponseDto;
import com.inity.tickenity.domain.reservation.service.ReservationService;
import com.inity.tickenity.global.response.BaseResponse;
import com.inity.tickenity.global.response.ResultCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;

    @PostMapping
    public BaseResponse<ReservationIdResponseDto> createReservation(
            HttpServletRequest httpServletRequest,
            @RequestBody ReservationCreateRequestDto reservationCreateRequestDto
    ) {
        Long userId = (Long) httpServletRequest.getAttribute("userId");
        return BaseResponse.success(reservationService.createReservation(userId, reservationCreateRequestDto), ResultCode.CREATED);
    }

    @GetMapping
    public BaseResponse<PageResponseDto<MyReservationResponse>> getMyReservation(
            HttpServletRequest httpServletRequest,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Long userId = (Long) httpServletRequest.getAttribute("userId");

        return BaseResponse.success(reservationService.getMyReservation(userId, page - 1, size), ResultCode.OK);
    }

    @GetMapping("/{reservationId}")
    public BaseResponse<ReservationDetailResponseDto> getDetailReservation(
            @PathVariable Long reservationId
    ) {
        return BaseResponse.success(reservationService.getDetailReservation(reservationId), ResultCode.OK);
    }

    @PatchMapping("/{reservationId}")
    public BaseResponse<Void> cancelReservation(
            @PathVariable Long reservationId
    ) {
        reservationService.cancelReservation(reservationId);
        return BaseResponse.success(ResultCode.NO_CONTENT);
    }
}
