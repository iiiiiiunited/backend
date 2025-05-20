package com.inity.tickenity.domain.concertvenue;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.inity.tickenity.domain.concert.entity.Concert;

public interface ConcertVenueRepository extends JpaRepository<ConcertVenue, Long> {
	List<ConcertVenue> findByConcert(Concert concert);
}
