package com.inity.tickenity.domain.reservation.controller;

import com.inity.tickenity.domain.reservation.dto.reqeust.ReservationCreateRequestDto;
import com.inity.tickenity.domain.reservation.dto.response.ReservationIdResponseDto;
import com.inity.tickenity.domain.reservation.service.ReservationService;
import com.inity.tickenity.global.response.BaseResponse;
import com.inity.tickenity.global.response.ResultCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;

    @PostMapping("/reservations")
    public BaseResponse<ReservationIdResponseDto> createReservation(
            HttpServletRequest httpServletRequest,
            @RequestBody ReservationCreateRequestDto reservationCreateRequestDto
    ) {
        Long userId = (Long) httpServletRequest.getAttribute("userId");
        return BaseResponse.success(reservationService.createReservation(userId,reservationCreateRequestDto), ResultCode.OK);
    }
}
