package com.inity.tickenity.domain.venue.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.inity.tickenity.domain.concert.entity.Concert;
import com.inity.tickenity.domain.concertvenue.ConcertVenue;
import com.inity.tickenity.domain.venue.entity.Venue;

public interface VenueRepository extends JpaRepository<Venue, Long> {
	List<Venue> findByConcertVenue(ConcertVenue cv);
}
