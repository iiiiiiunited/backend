package com.inity.tickenity.domain.venue.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.inity.tickenity.domain.common.repository.BaseRepository;
import com.inity.tickenity.domain.concert.entity.Concert;
import com.inity.tickenity.domain.venue.entity.Venue;

public interface VenueRepository extends BaseRepository<Venue, Long> {
	@Query("SELECT cv FROM ConcertVenue cv " +
	"JOIN FETCH cv.venue v " +
	"JOIN FETCH cv.concert c " +
	"WHERE c.id = :concertId")
	List<Venue> findAllByConcert(@Param("concert") Concert concert);
}
