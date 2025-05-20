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

    @Query("SELECT DISTINCT new com.inity.tickenity.domain.reservation.dto.response.MyReservationResponse(r.id, r.createdAt, r.schedule.concert.title, v.name ,r.reservationStatus) " +
            "FROM Reservation r " +
            "JOIN ConcertVenue c ON c.concert.id = r.schedule.concert.id " +
            "JOIN c.venue v " +
            "WHERE r.user.id = :userId")
    Page<MyReservationResponse> findAllByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT new com.inity.tickenity.domain.reservation.dto.response.ReservationDetailResponseDto(r.id, r.schedule.startTime, r.schedule.endTime, r.reservationStatus, r.paymentStatus) " +
            "FROM Reservation r WHERE r.id = :reservationId")
    ReservationDetailResponseDto findByReservationWithDto(@Param("reservationId") Long reservationId);

    @Query("""
    SELECT r.seatInformation.number FROM Reservation r
    WHERE r.schedule.id = :scheduleId AND r.reservationStatus = 'RESERVED'
""")
    Set<String> findReservedSeatNumbers(@Param("scheduleId") Long scheduleId);


}
