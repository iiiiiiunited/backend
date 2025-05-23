package com.inity.tickenity.domain.reservation.controller;

import com.inity.tickenity.domain.common.annotation.Auth;
import com.inity.tickenity.domain.common.dto.AuthUser;
import com.inity.tickenity.domain.common.dto.PageResponseDto;
import com.inity.tickenity.domain.reservation.dto.reqeust.ReservationCreateRequestDto;
import com.inity.tickenity.domain.reservation.dto.response.MyReservationResponse;
import com.inity.tickenity.domain.reservation.dto.response.ReservationDetailResponseDto;
import com.inity.tickenity.domain.reservation.dto.response.ReservationIdResponseDto;
import com.inity.tickenity.domain.reservation.service.ReservationService;
import com.inity.tickenity.global.response.BaseResponse;
import com.inity.tickenity.global.response.ResultCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;

    @PostMapping("/lettuce")
    public BaseResponse<ReservationIdResponseDto> createReservationWithLettuce(
            @Auth AuthUser authUser,
            @RequestBody ReservationCreateRequestDto reservationCreateRequestDto
    ) {
        return BaseResponse.success(
                reservationService.createReservationWithLettuce(authUser.id(), reservationCreateRequestDto),
                ResultCode.CREATED
        );
    }

    @PostMapping("/redisson")
    public BaseResponse<ReservationIdResponseDto> createReservationWithRedisson(
            @Auth AuthUser authUser,
            @RequestBody ReservationCreateRequestDto reservationCreateRequestDto
    ) {
        return BaseResponse.success(
                reservationService.createReservationWithRedisson(authUser.id(), reservationCreateRequestDto),
                ResultCode.CREATED
        );
    }

    @GetMapping
    public BaseResponse<PageResponseDto<MyReservationResponse>> getMyReservation(
            @Auth AuthUser authUser,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return BaseResponse.success(
                reservationService.getMyReservation(authUser.id(), page - 1, size),
                ResultCode.OK
        );
    }

    @GetMapping("/{reservationId}")
    public BaseResponse<ReservationDetailResponseDto> getDetailReservation(
            @PathVariable Long reservationId
    ) {
        return BaseResponse.success(
                reservationService.getDetailReservation(reservationId),
                ResultCode.OK
        );
    }

    @PatchMapping("/{reservationId}")
    public BaseResponse<Void> cancelReservation(
            @PathVariable Long reservationId
    ) {
        reservationService.cancelReservation(reservationId);
        return BaseResponse.success(ResultCode.NO_CONTENT);
    }
}
