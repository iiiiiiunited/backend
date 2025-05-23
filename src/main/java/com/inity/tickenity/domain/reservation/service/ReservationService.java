package com.inity.tickenity.domain.reservation.service;

import com.inity.tickenity.domain.common.dto.PageResponseDto;
import com.inity.tickenity.domain.redisRock.aop.LettuceLock;
import com.inity.tickenity.domain.redisRock.aop.RedissonLock;
import com.inity.tickenity.domain.redisRock.service.LockService;
import com.inity.tickenity.domain.reservation.dto.reqeust.ReservationCreateRequestDto;
import com.inity.tickenity.domain.reservation.dto.response.MyReservationResponse;
import com.inity.tickenity.domain.reservation.dto.response.ReservationDetailResponseDto;
import com.inity.tickenity.domain.reservation.dto.response.ReservationIdResponseDto;
import com.inity.tickenity.domain.reservation.entity.Reservation;
import com.inity.tickenity.domain.reservation.repository.ReservationRepository;
import com.inity.tickenity.domain.schedule.entity.Schedule;
import com.inity.tickenity.domain.schedule.repository.ScheduleRepository;
import com.inity.tickenity.domain.seat.entity.Seat;
import com.inity.tickenity.domain.seat.enums.SeatStatus;
import com.inity.tickenity.domain.seat.repository.SeatRepository;
import com.inity.tickenity.domain.user.entity.User;
import com.inity.tickenity.domain.user.repository.UserRepository;
import com.inity.tickenity.global.exception.BusinessException;
import com.inity.tickenity.global.response.ResultCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final ScheduleRepository scheduleRepository;
    private final PlatformTransactionManager transactionManager;
    private final SeatRepository seatRepository;

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

        // 1. 좌석 테이블에 좌석이 등록 되어 있는지 확인.
        Seat findSeat = seatRepository.findByIdOrElseThrow(reservationCreateRequestDto.seatId());


        // 2. 좌석 상태가 RESERVATIONS 인지 확인
        if (findSeat.getSeatStatus() != SeatStatus.AVAILABLE) {
            throw new BusinessException(ResultCode.DB_FAIL, "이미 예약된 좌석입니다.");
        }

        // 3. 좌석 속 일정 정보 가져오기
        Schedule findSchedule = scheduleRepository.findBySeatId(findSeat.getId());

        // 4. Reservation 생성
        Reservation reservation = Reservation.builder()
                .user(findUser)
                .schedule(findSchedule)
                .seat(findSeat)
                .build();

        // 5. 좌석 Status 변경
        findSeat.updateSeatToReserved();

        // 6. Reservation 저장
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
        Seat seat = seatRepository.findByReservationId(reservationId);
        seat.updateSeatToAvailable();
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
        String key = "lock:" + reservationCreateRequestDto.seatId();
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

    @LettuceLock(userId = "#userId", seatId = "#seatId")
    public ReservationIdResponseDto createReservationWithLettuceAop(
            Long userId,
            Long seatId
    ) {
        User findUser = userRepository.findByIdOrElseThrow(userId);

        // 1. 좌석 테이블에 좌석이 등록 되어 있는지 확인.
        Seat findSeat = seatRepository.findByIdOrElseThrow(seatId);

        // 2. 좌석 상태가 RESERVATIONS 인지 확인
        if (findSeat.getSeatStatus() != SeatStatus.AVAILABLE) {
            throw new BusinessException(ResultCode.DB_FAIL, "이미 예약된 좌석입니다.");
        }

        // 3. 좌석 속 일정 정보 가져오기
        Schedule findSchedule = scheduleRepository.findBySeatId(findSeat.getId());

        // 4. Reservation 생성
        Reservation reservation = Reservation.builder()
                .user(findUser)
                .schedule(findSchedule)
                .seat(findSeat)
                .build();

        // 5. 좌석 Status 변경
        findSeat.updateSeatToReserved();

        // 6. Reservation 저장
        Reservation saved = reservationRepository.save(reservation);
        return ReservationIdResponseDto.of(saved.getId());
    }

    @RedissonLock(userId = "#userId", seatId = "#seatId")
    public ReservationIdResponseDto createReservationWithRedisson(
            Long userId,
            Long seatId
    ) {
        User findUser = userRepository.findByIdOrElseThrow(userId);

        // 1. 좌석 테이블에 좌석이 등록 되어 있는지 확인.
        Seat findSeat = seatRepository.findByIdOrElseThrow(seatId);

        // 2. 좌석 상태가 RESERVATIONS 인지 확인
        if (findSeat.getSeatStatus() != SeatStatus.AVAILABLE) {
            throw new BusinessException(ResultCode.DB_FAIL, "이미 예약된 좌석입니다.");
        }

        // 3. 좌석 속 일정 정보 가져오기
        Schedule findSchedule = scheduleRepository.findBySeatId(findSeat.getId());

        // 4. Reservation 생성
        Reservation reservation = Reservation.builder()
                .user(findUser)
                .schedule(findSchedule)
                .seat(findSeat)
                .build();

        // 5. 좌석 Status 변경
        findSeat.updateSeatToReserved();

        // 6. Reservation 저장
        Reservation saved = reservationRepository.save(reservation);
        return ReservationIdResponseDto.of(saved.getId());
    }

    @Transactional
    public ReservationIdResponseDto createReservationWithPessimisticLock(Long userId, ReservationCreateRequestDto dto) {
        User findUser = userRepository.findByIdOrElseThrow(userId);

        // 1. 좌석 테이블에 좌석이 등록 되어 있는지 확인.
        Seat findSeat = seatRepository.findByIdWithPessimisticLock(dto.seatId()).orElseThrow(() ->
                new BusinessException(ResultCode.NOT_FOUND, "해당 Entity를 찾을 수 없습니다. id = " + dto.seatId()));

        // 2. 좌석 상태가 RESERVATIONS 인지 확인
        if (findSeat.getSeatStatus() != SeatStatus.AVAILABLE) {
            throw new BusinessException(ResultCode.DB_FAIL, "이미 예약된 좌석입니다.");
        }

        // 3. 좌석 속 일정 정보 가져오기
        Schedule findSchedule = scheduleRepository.findBySeatId(findSeat.getId());

        // 4. Reservation 생성
        Reservation reservation = Reservation.builder()
                .user(findUser)
                .schedule(findSchedule)
                .seat(findSeat)
                .build();

        // 5. 좌석 Status 변경
        findSeat.updateSeatToReserved();

        // 6. Reservation 저장
        Reservation saved = reservationRepository.save(reservation);
        return ReservationIdResponseDto.of(saved.getId());
    }
}
