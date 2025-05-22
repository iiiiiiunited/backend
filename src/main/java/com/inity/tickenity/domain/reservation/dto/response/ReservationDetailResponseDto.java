package com.inity.tickenity.domain.reservation.dto.response;

import com.inity.tickenity.domain.reservation.enums.PaymentStatus;
import com.inity.tickenity.domain.reservation.enums.ReservationStatus;
import com.inity.tickenity.domain.seat.enums.SeatGradeType;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ReservationDetailResponseDto {

    Long reservationId;

    String concertName;

    String venueName;

    LocalDateTime concertStartDateTime;

    LocalDateTime concertEndDateTime;

    ReservationStatus reservationStatus;

    PaymentStatus paymentStatus;

    String seatNumber;

    SeatGradeType seatRank;

    public ReservationDetailResponseDto(
            Long reservationId,
            String concertName,
            String venueName,
            LocalDateTime concertStartDateTime,
            LocalDateTime concertEndDateTime,
            ReservationStatus reservationStatus,
            PaymentStatus paymentStatus,
            String seatNumber,
            SeatGradeType seatRank
    ) {
        this.reservationId = reservationId;
        this.concertName = concertName;
        this.venueName = venueName;
        this.concertStartDateTime = concertStartDateTime;
        this.concertEndDateTime = concertEndDateTime;
        this.reservationStatus = reservationStatus;
        this.paymentStatus = paymentStatus;
        this.seatNumber = seatNumber;
        this.seatRank = seatRank;
    }
}
