package com.inity.tickenity.domain.reservation.dto.response;

public record ReservationIdResponseDto(
        Long reservationId
) {
    public static ReservationIdResponseDto of(Long reservationId) {
        return new ReservationIdResponseDto(reservationId);
    }
}
