package com.inity.tickenity.domain.concert.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.inity.tickenity.domain.common.repository.BaseRepository;
import com.inity.tickenity.domain.concert.entity.Concert;
import com.inity.tickenity.domain.concert.enums.Genre;

public interface ConcertRepository extends BaseRepository<Concert, Long> {
	List<Concert> findAllByGenre(Genre genre);


	@Query("SELECT c FROM Concert c " +
		"LEFT JOIN FETCH c.concertVenues cv " +
		"LEFT JOIN FETCH cv.venue " +
		"WHERE c.id = :id")
	Optional<Concert> findByIdWithVenues(@Param("id") Long id);
}
