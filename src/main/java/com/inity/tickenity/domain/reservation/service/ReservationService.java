package com.inity.tickenity.domain.reservation.service;

import com.inity.tickenity.domain.common.dto.PageResponseDto;
import com.inity.tickenity.domain.reservation.dto.reqeust.ReservationCreateRequestDto;
import com.inity.tickenity.domain.reservation.dto.response.MyReservationResponse;
import com.inity.tickenity.domain.reservation.dto.response.ReservationDetailResponseDto;
import com.inity.tickenity.domain.reservation.dto.response.ReservationIdResponseDto;
import com.inity.tickenity.domain.reservation.entity.Reservation;
import com.inity.tickenity.domain.reservation.repository.ReservationRepository;
import com.inity.tickenity.domain.user.entity.User;
import com.inity.tickenity.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    public PageResponseDto<MyReservationResponse> getMyReservation(
            Long userId,
            int page,
            int size
    ) {

        Pageable pageable = PageRequest.of(page, size);

        Page<MyReservationResponse> pageReservation = reservationRepository.findByUser_Id(userId, pageable);

        return PageResponseDto.toDto(pageReservation);
    }

    // 일단은 예매 등록일과 수정일로 StartDate 와 EndDate 를 대체한다.
    public ReservationDetailResponseDto getDetailReservation(Long reservationId) {
        return reservationRepository.findByReservationWithDto(reservationId);
    }
}
