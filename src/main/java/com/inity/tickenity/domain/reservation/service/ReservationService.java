package com.inity.tickenity.domain.reservation.service;

import com.inity.tickenity.domain.common.dto.PageResponseDto;
import com.inity.tickenity.domain.redisRock.service.LockService;
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

    // Lock Service
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

    // Lettuce 저장
    public void createReservationWithLettuce(
            Long userId,
            ReservationCreateRequestDto reservationCreateRequestDto
    ) {
        String key = "lock:" + reservationCreateRequestDto.scheduleId() + reservationCreateRequestDto.seatInformationId();
        String value = userId.toString();

        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
        boolean locked = false;
        try {
            // 1. 락 점유
            locked = lockService.lock(key, value);
            if (!locked) {
                throw new IllegalStateException("이미 예매를 진행하고 있습니다. : " + key);
            }

            // 2. DB 저장
            createReservation(userId, reservationCreateRequestDto);

            // 3. DB Commit
            transactionManager.commit(status);

        } catch (Exception e) {
            // 락 획득 실패, Spring Data 문제, 커밋-롤백 에러, InterruptedException, 기타 예외
            if (!status.isCompleted()) {
                transactionManager.rollback(status);
            }
            throw new RuntimeException("트랜잭션 실패", e);
        } finally {
            if (locked) {
                try {
                    lockService.unlock(key);
                } catch (Exception e) {
                    // 네트워크/서버 문제 (접속 끊김, 타임아웃), 락 키가 없거나 이미 만료됨, 클라이언트 라이브러리 문제
                }
            }
        }
    }
}
