package com.inity.tickenity.domain.reservation.repository;

import com.inity.tickenity.domain.common.repository.BaseRepository;
import com.inity.tickenity.domain.reservation.dto.response.MyReservationResponse;
import com.inity.tickenity.domain.reservation.dto.response.ReservationDetailResponseDto;
import com.inity.tickenity.domain.reservation.entity.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface ReservationRepository extends BaseRepository<Reservation, Long> {

    @Query("""
    SELECT DISTINCT new com.inity.tickenity.domain.reservation.dto.response.MyReservationResponse(
        r.id,
        r.createdAt,
        r.schedule.concert.title,
        cv.venue.name,
        r.reservationStatus
    )
    FROM Reservation r
    JOIN Schedule s ON s.id = r.schedule.id
    JOIN Concert c ON c.id = s.concert.id
    JOIN ConcertVenue cv ON cv.concert.id = c.id
    WHERE r.user.id = :userId
    """)
    Page<MyReservationResponse> findAllByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT DISTINCT new com.inity.tickenity.domain.reservation.dto.response.ReservationDetailResponseDto(r.id, r.schedule.concert.title, c.venue.name, r.schedule.startTime, r.schedule.endTime, r.reservationStatus, r.paymentStatus, r.seat.number, r.seat.grade) " +
            "FROM Reservation r " +
            "JOIN ConcertVenue c ON c.concert.id = r.schedule.concert.id " +
            "WHERE r.id = :reservationId")
    ReservationDetailResponseDto findByReservationWithDto(@Param("reservationId") Long reservationId);

    @Query("""
    SELECT r.seat.number FROM Reservation r
    WHERE r.schedule.id = :scheduleId AND r.reservationStatus = 'RESERVED'
    """)
    Set<String> findReservedSeatNumbers(@Param("scheduleId") Long scheduleId);

//    boolean existsBySchedule_IdAndSeatInformation_Id(Long scheduleId, Long seatInformationId);

    long count();
}
