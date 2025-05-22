package com.inity.tickenity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.inity.tickenity.domain.common.annotation.Auth;
import com.inity.tickenity.domain.concert.entity.Concert;
import com.inity.tickenity.domain.concert.repository.ConcertRepository;
import com.inity.tickenity.domain.concert.service.ConcertService;
import com.inity.tickenity.domain.seat.entity.SeatGradePrice;
import com.inity.tickenity.domain.seat.entity.SeatInformation;
import com.inity.tickenity.domain.seat.enums.SeatGradeType;
import com.inity.tickenity.domain.seat.repository.SeatGradePriceRepository;
import com.inity.tickenity.domain.seat.repository.SeatInformationRepository;
import com.inity.tickenity.domain.venue.entity.Venue;
import com.inity.tickenity.domain.venue.repository.VenueRepository;
import com.inity.tickenity.domain.venue.service.VenueService;

@SpringBootTest
public class ReservationTest {

	@Autowired
	private VenueService venueService;

	@Autowired
	private ConcertService concertService;
	@Autowired
	private VenueRepository venueRepository;
	@Autowired
	private ConcertRepository concertRepository;
	@Autowired
	private SeatGradePriceRepository seatGradePriceRepository;
	@Autowired
	private SeatInformationRepository seatInformationRepository;

	@Test
	void test1() {
		Concert concert = concertRepository.findById(1L).orElseThrow();

		SeatGradePrice gradeA = new SeatGradePrice(concert, 50000, SeatGradeType.A);
		SeatGradePrice gradeB = new SeatGradePrice(concert, 30000, SeatGradeType.B);

		seatGradePriceRepository.save(gradeA);
		seatGradePriceRepository.save(gradeB);
	}

	@Test
	void dummySeatInformation() {
		Venue venue = venueRepository.findById(1L).orElseThrow();
		SeatInformation si = new SeatInformation(venue, SeatGradeType.A, "A3");
		seatInformationRepository.save(si);
	}
}
