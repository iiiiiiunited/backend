package com.inity.tickenity.domain.reservation.dto.reqeust;

import jakarta.validation.constraints.NotEmpty;

public record ReservationCreateRequestDto(
        @NotEmpty
        Long seatId
) {
}
