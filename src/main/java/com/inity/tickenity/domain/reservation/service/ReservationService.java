package com.inity.tickenity.domain.reservation.service;

import com.inity.tickenity.domain.common.dto.PageResponseDto;
import com.inity.tickenity.domain.reservation.dto.reqeust.ReservationCreateRequestDto;
import com.inity.tickenity.domain.reservation.dto.response.MyReservationResponse;
import com.inity.tickenity.domain.reservation.dto.response.ReservationDetailResponseDto;
import com.inity.tickenity.domain.reservation.dto.response.ReservationIdResponseDto;
import com.inity.tickenity.domain.reservation.entity.Reservation;
import com.inity.tickenity.domain.reservation.repository.ReservationRepository;
import com.inity.tickenity.domain.schedule.entity.Schedule;
import com.inity.tickenity.domain.schedule.repository.ScheduleRepository;
import com.inity.tickenity.domain.seat.entity.SeatInformation;
import com.inity.tickenity.domain.seat.repository.SeatInformationRepository;
import com.inity.tickenity.domain.user.entity.User;
import com.inity.tickenity.domain.user.repository.UserRepository;
import com.inity.tickenity.global.exception.BusinessException;
import com.inity.tickenity.global.lock.LockService;
import com.inity.tickenity.global.lock.LettuceLock;
import com.inity.tickenity.global.lock.RedissonLock;
import com.inity.tickenity.global.lock.RedissonLockService;
import com.inity.tickenity.global.response.ResultCode;
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
    private final ScheduleRepository scheduleRepository;
    private final SeatInformationRepository seatInformationRepository;
    private final RedissonLockService redissonLockService;
    private final LockService lockService;

    /**
     * Reservation 을 생성
     *
     * @param userId
     * @param reservationCreateRequestDto
     * @return ReservationIdResponseDto
     */
    @Transactional
    @LettuceLock(key = "'reservation:' + #reservationCreateRequestDto.scheduleId() + ':' + #reservationCreateRequestDto.seatInformationId()", timeout = 3000)
    public ReservationIdResponseDto createReservationWithLettuce(
            Long userId,
            ReservationCreateRequestDto reservationCreateRequestDto
    ) {
        User findUser = userRepository.findByIdOrElseThrow(userId);
        Schedule findSchedule = scheduleRepository.findByIdOrElseThrow(reservationCreateRequestDto.scheduleId());
        SeatInformation findSeatInformation = seatInformationRepository.findByIdOrElseThrow(reservationCreateRequestDto.seatInformationId());

        if (reservationRepository.existsBySchedule_IdAndSeatInformation_Id(findSchedule.getId(), findSeatInformation.getId())) {
            throw new BusinessException(ResultCode.DB_FAIL, "이미 예약된 좌석입니다.");
        }

        Reservation reservation = Reservation.builder()
                .user(findUser)
                .schedule(findSchedule)
                .seatInformation(findSeatInformation)
                .build();

        Reservation saved = reservationRepository.save(reservation);
        return ReservationIdResponseDto.of(saved.getId());
    }


    @Transactional
    @RedissonLock(key = "'reservation:' + #reservationCreateRequestDto.scheduleId() + ':' + #reservationCreateRequestDto.seatInformationId()", waitTime = 3000, leaseTime = 2000)
    public ReservationIdResponseDto createReservationWithRedisson(Long userId, ReservationCreateRequestDto reservationCreateRequestDto) {
        String lockKey = "reservation:" + reservationCreateRequestDto.scheduleId() + ":" + reservationCreateRequestDto.seatInformationId();
        return redissonLockService.executeWithLock(lockKey, 3000, 2000, () -> {
            if (reservationRepository.existsBySchedule_IdAndSeatInformation_Id(reservationCreateRequestDto.scheduleId(), reservationCreateRequestDto.seatInformationId())) {
                throw new BusinessException(ResultCode.DB_FAIL, "이미 예약된 좌석입니다.");
            }
            Reservation reservation = Reservation.builder()
                    .user(userRepository.findByIdOrElseThrow(userId))
                    .schedule(scheduleRepository.findByIdOrElseThrow(reservationCreateRequestDto.scheduleId()))
                    .seatInformation(seatInformationRepository.findByIdOrElseThrow(reservationCreateRequestDto.seatInformationId()))
                    .build();

            Reservation saved = reservationRepository.save(reservation);
            return ReservationIdResponseDto.of(saved.getId());
        });
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

        Page<MyReservationResponse> pageReservation = reservationRepository.findAllByUserId(userId, pageable);

        return PageResponseDto.toDto(pageReservation);
    }

    /**
     * 단건 예매 조회
     *
     * @param reservationId
     * @return ReservationDetailResponseDto
     */
    public ReservationDetailResponseDto getDetailReservation(Long reservationId) {
        return reservationRepository.findByReservationWithDto(reservationId);
    }

    /**
     * 예약 취소
     * 예약 상태만 예약 취소 상태로 변경
     *
     * @param reservationId
     */
    @Transactional
    public void cancelReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findByIdOrElseThrow(reservationId);
        reservation.updateStatusToCancelled();
    }

    /**
     * Reservation 카운트 해주는 메서드
     */
    public Long countReservation() {
        Long result = reservationRepository.count();
        System.out.println("\n\n\n====================\n");
        System.out.println(result);
        System.out.println("\n====================\n\n\n");
        return result;
    }
}