package com.inity.tickenity.domain.concert.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.inity.tickenity.domain.concert.entity.Concert;

public interface ConcertRepository extends JpaRepository<Concert, Long> {
	List<Concert> findByGenre(String genre);
}
