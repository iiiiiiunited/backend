package com.inity.tickenity.domain.reservation.service;

import com.inity.tickenity.domain.reservation.dto.reqeust.ReservationCreateRequestDto;
import com.inity.tickenity.domain.reservation.dto.response.ReservationIdResponseDto;
import com.inity.tickenity.domain.reservation.entity.Reservation;
import com.inity.tickenity.domain.reservation.repository.ReservationRepository;
import com.inity.tickenity.domain.user.entity.User;
import com.inity.tickenity.domain.user.repository.UserRepository;
import com.inity.tickenity.global.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;

    public ReservationIdResponseDto createReservation(
            Long userId,
            ReservationCreateRequestDto reservationCreateRequestDto
    ) {
        User finduser = userRepository.findByIdOrElseThrow(userId);

        Reservation reservation = Reservation.builder()
                .user(finduser)
                .scheduleId(reservationCreateRequestDto.scheduleId())
                .seatInformationId(reservationCreateRequestDto.seatInformationId())
                .build();

        Reservation saved = reservationRepository.save(reservation);

        return ReservationIdResponseDto.of(saved.getId());
    }
}
