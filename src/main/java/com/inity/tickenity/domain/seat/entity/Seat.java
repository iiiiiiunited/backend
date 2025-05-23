package com.inity.tickenity.domain.seat.entity;

import com.inity.tickenity.domain.schedule.entity.Schedule;
import com.inity.tickenity.domain.seat.enums.SeatGradeType;
import com.inity.tickenity.domain.seat.enums.SeatStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "seats")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeatGradeType grade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeatStatus seatStatus = SeatStatus.AVAILABLE;
    ;

    @Column(name = "number", nullable = false)
    private String number;

    public Seat(SeatGradeType grade, Schedule schedule, SeatStatus seatStatus, String number) {
        this.grade = grade;
        this.schedule = schedule;
        this.seatStatus = seatStatus;
        this.number = number;
    }

    public void updateSeatToReserved() {
        this.seatStatus = SeatStatus.RESERVED;
    }

    public void updateSeatToAvailable() {
        this.seatStatus = SeatStatus.AVAILABLE;
    }
}

