package com.inity.tickenity.domain.reservation.entity;

import com.inity.tickenity.domain.common.entity.BaseTimeEntity;
import com.inity.tickenity.domain.reservation.enums.PaymentStatus;
import com.inity.tickenity.domain.reservation.enums.ReservationStatus;
import com.inity.tickenity.domain.schedule.entity.Schedule;
import com.inity.tickenity.domain.seat.entity.SeatInformation;
import com.inity.tickenity.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Reservation extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus reservationStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne
    @JoinColumn(name = "seat_id", unique = true)
    private SeatInformation seatInformation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;

    // Builder
    @Builder
    public Reservation(User user, Schedule schedule, SeatInformation seatInformation) {
        this.user = user;
        this.reservationStatus = ReservationStatus.PENDING;
        this.paymentStatus = PaymentStatus.PENDING;
        this.schedule = schedule;
        this.seatInformation = seatInformation;
    }

    // Reservation Status 수정
    public void updateStatusToCancelled() {
        this.reservationStatus = ReservationStatus.CANCELLED;
    }
}
