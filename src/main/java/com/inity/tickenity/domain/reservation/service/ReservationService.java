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
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;

    /**
     * Reservation 을 생성
     *
     * @param userId
     * @param reservationCreateRequestDto
     * @return ReservationIdResponseDto 
     */
    @Transactional
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

    /**
     * 로그인한 유저의 예매 내역을 모두 조회
     *
     * @param userId
     * @param page
     * @param size
     * @return PageResponseDto, 페이징으로 예매 내역 조회
     */
    public PageResponseDto<MyReservationResponse> getMyReservation(
            Long userId,
            int page,
            int size
    ) {

        Pageable pageable = PageRequest.of(page, size);

        Page<MyReservationResponse> pageReservation = reservationRepository.findByUser_Id(userId, pageable);

        return PageResponseDto.toDto(pageReservation);
    }

    /**
     * 단건 예매 조회
     *
     * @param reservationId
     * @return ReservationDetailResponseDto
     */
    // Concert 의 StartDate 와 EndDate 를 예매 등록일과 수정일로 대체한다.
    public ReservationDetailResponseDto getDetailReservation(Long reservationId) {
        return reservationRepository.findByReservationWithDto(reservationId);
    }

    /**
     * 예약 취소
     *
     * @param reservationId
     */
    @Transactional
    public void cancelReservation(Long reservationId) {
        // 예약 상태만 예약 취소 상태로 바꿀 것이다.
        Reservation reservation = reservationRepository.findByIdOrElseThrow(reservationId);
        reservation.updateStatusToCancelled();
    }
}
