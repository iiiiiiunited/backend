package com.inity.tickenity.domain.seat.repository;

import com.inity.tickenity.domain.common.repository.BaseRepository;
import com.inity.tickenity.domain.reservation.entity.Reservation;
import com.inity.tickenity.domain.seat.entity.Seat;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SeatRepository extends BaseRepository<Seat, Long> {

//    @Query(value = """
//    SELECT si.*
//    FROM seats s
//    JOIN venues v ON si.venue_id = v.id
//    JOIN concert_venue cv ON cv.venue_id = v.id
//    JOIN concerts c ON c.id = cv.concert_id
//    WHERE c.id = :concertId
//    """, nativeQuery = true)
//    List<Seat> findByConcertId(@Param("concertId") Long concertId);

    @Query("SELECT s FROM Seat s JOIN Reservation r ON s.id = r.seat.id WHERE r.id = :reservationId")
    Seat findByReservationId(@Param("reservationId") Long reservationId);

    // PessimisticLock
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "jakarta.persistence.lock.timeout", value = "3000")})  // timeout 설정 (3초)
    @Query("select s from Seat s where s.id = :seatId ")
    Optional<Seat> findByIdWithPessimisticLock(@Param("seatId") Long seatId);

}
