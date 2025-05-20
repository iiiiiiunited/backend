package com.inity.tickenity.domain.concert.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.inity.tickenity.domain.common.repository.BaseRepository;
import com.inity.tickenity.domain.concert.entity.Concert;
import com.inity.tickenity.domain.concert.enums.Genre;
import com.inity.tickenity.domain.concertvenue.ConcertVenue;

public interface ConcertRepository extends BaseRepository<Concert, Long> {
	List<Concert> findAllByGenre(Genre genre);

	@Query("SELECT cv FROM ConcertVenue cv " +
		"JOIN FETCH cv.concert c " +
		"JOIN FETCH cv.venue v " +
		"WHERE c.id = :concertId")
	List<ConcertVenue> findAllByConcertIdWithVenue(@Param("concertId") Long concertId);
}
