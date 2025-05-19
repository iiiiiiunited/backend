package com.inity.tickenity.domain.reservation.dto.response;

import com.inity.tickenity.domain.reservation.enums.ReservationStatus;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class MyReservationResponse {

    Long reservationId;

    LocalDate reservationDate;

//    String concertName;
//
//    String venueName;

    ReservationStatus reservationStatus;

//    public MyReservationResponse(Long reservationId, LocalDateTime reservationDate, String concertName, String venueName, String reservationStatus) {
    public MyReservationResponse(Long reservationId, LocalDateTime reservationDate, ReservationStatus reservationStatus) {
        this.reservationId = reservationId;
        this.reservationDate = reservationDate.toLocalDate();
//        this.concertName = concertName;
//        this.venueName = venueName;
        this.reservationStatus = reservationStatus;
    }
}


