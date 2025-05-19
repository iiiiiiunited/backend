package com.inity.tickenity.domain.reservation.repository;

import com.inity.tickenity.domain.common.repository.BaseRepository;
import com.inity.tickenity.domain.reservation.dto.response.MyReservationResponse;
import com.inity.tickenity.domain.reservation.entity.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReservationRepository extends BaseRepository<Reservation, Long> {

    @Query("SELECT new com.inity.tickenity.domain.reservation.dto.response.MyReservationResponse(r.id, r.createdAt, r.reservationStatus) " +
            "FROM Reservation r WHERE r.user.id = :userId")
    Page<MyReservationResponse> findByUser_Id(@Param("userId") Long userId, Pageable pageable);
}
