package com.inity.tickenity.domain.seat.entity;

import com.inity.tickenity.domain.seat.enums.SeatGradeType;
import com.inity.tickenity.domain.venue.entity.Venue;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "seat_informations")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SeatInformation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venue_id", nullable = false)
    private Venue venue;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeatGradeType grade;


    @Column(name = "number", nullable = false)
    private String number;

    public SeatInformation(Venue venue, SeatGradeType grade, String number) {
        this.venue = venue;
        this.grade = grade;
        this.number = number;
    }
}
