package com.inity.tickenity.domain.venue.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.inity.tickenity.domain.common.repository.BaseRepository;
import com.inity.tickenity.domain.concert.entity.Concert;
import com.inity.tickenity.domain.concertvenue.ConcertVenue;
import com.inity.tickenity.domain.venue.entity.Venue;

public interface VenueRepository extends BaseRepository<Venue, Long> {
	@Query("SELECT v FROM Venue v " +
		"JOIN FETCH v.concertVenues cv " +
		"WHERE cv.concert = :concert")
	List<Venue> findAllByConcert(@Param("concert") Concert concert);
}
