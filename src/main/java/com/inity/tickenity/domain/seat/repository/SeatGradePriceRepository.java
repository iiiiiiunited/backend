package com.inity.tickenity.domain.seat.repository;

import com.inity.tickenity.domain.common.repository.BaseRepository;
import com.inity.tickenity.domain.seat.entity.SeatGradePrice;


import java.util.List;

public interface SeatGradePriceRepository extends BaseRepository<SeatGradePrice, Long> {

    List<SeatGradePrice> findByConcertId(Long concertId);
}
