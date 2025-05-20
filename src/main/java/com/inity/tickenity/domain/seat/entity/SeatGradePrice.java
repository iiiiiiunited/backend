package com.inity.tickenity.domain.seat.entity;

import com.inity.tickenity.domain.concert.entity.Concert;
import com.inity.tickenity.domain.seat.enums.SeatGradeType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "seat_grade_price")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SeatGradePrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_id", nullable = false)
    private Concert concert;

    @Column(name = "price", nullable = false)
    private int price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeatGradeType grade;

}
