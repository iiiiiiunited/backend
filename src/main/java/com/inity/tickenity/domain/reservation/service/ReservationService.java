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
import com.inity.tickenity.global.response.ResultCode;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final ScheduleRepository scheduleRepository;
    private final SeatInformationRepository seatInformationRepository;
    private final LockService lockService;

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
        String lockKey = "reservation:" + reservationCreateRequestDto.scheduleId() + ":" + reservationCreateRequestDto.seatInformationId();
        String uuid = UUID.randomUUID().toString();
        AtomicReference<Reservation> savedReservation = new AtomicReference<>();

        // 2. 락 점유
        boolean locked = lockService.tryLock(lockKey, uuid, 3000);
        if (!locked) {
            throw new BusinessException(ResultCode.LOCK_FAIL, "락 획득 실패");
        }

        try {
            // 3. 비즈니스 로직 실행
            if (reservationRepository.existsBySchedule_IdAndSeatInformation_Id(
                    reservationCreateRequestDto.scheduleId(),
                    reservationCreateRequestDto.seatInformationId())) {
                throw new BusinessException(ResultCode.DB_FAIL, "이미 예약된 좌석입니다.");
            }

            Reservation reservation = Reservation.builder()
                    .user(userRepository.findByIdOrElseThrow(userId))
                    .schedule(scheduleRepository.findByIdOrElseThrow(reservationCreateRequestDto.scheduleId()))
                    .seatInformation(seatInformationRepository.findByIdOrElseThrow(reservationCreateRequestDto.seatInformationId()))
                    .build();

            savedReservation.set(reservationRepository.save(reservation));

            // 5. 커밋 후 락 해제 예약
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    lockService.unlock(lockKey, uuid);
                }
            });

        } catch (Exception e) {
            // 예외 발생 시 즉시 락 해제 (rollback 되더라도 unlock은 반드시 필요)
            lockService.unlock(lockKey, uuid);
            throw e;
        }

        return ReservationIdResponseDto.of(savedReservation.get().getId());
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