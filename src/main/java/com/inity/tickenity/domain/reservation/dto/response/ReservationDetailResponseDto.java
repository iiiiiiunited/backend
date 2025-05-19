package com.inity.tickenity.domain.reservation.dto.response;

import com.inity.tickenity.domain.reservation.enums.PaymentStatus;
import com.inity.tickenity.domain.reservation.enums.ReservationStatus;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ReservationDetailResponseDto {

    Long reservationId;

//    String concertName;
//
//    String venueName;

    LocalDateTime concertStartDateTime;

    LocalDateTime concertEndDateTime;

    ReservationStatus reservationStatus;

    PaymentStatus paymentStatus;

//    String seatNumber;
//
//    String seatRank;


    public ReservationDetailResponseDto(Long reservationId,
                                        LocalDateTime concertStartDateTime,
                                        LocalDateTime concertEndDateTime,
                                        ReservationStatus reservationStatus,
                                        PaymentStatus paymentStatus) {
        this.reservationId = reservationId;
        this.concertStartDateTime = concertStartDateTime;
        this.concertEndDateTime = concertEndDateTime;
        this.reservationStatus = reservationStatus;
        this.paymentStatus = paymentStatus;
    }
}
