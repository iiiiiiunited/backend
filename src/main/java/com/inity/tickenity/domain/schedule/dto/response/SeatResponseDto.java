package com.inity.tickenity.domain.schedule.dto.response;

import com.inity.tickenity.domain.seat.enums.SeatGradeType;

public record SeatResponseDto(
        String seatId,
        SeatGradeType grade,
        int price,
        boolean isReserved
) {
}
