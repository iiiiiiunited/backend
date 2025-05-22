package com.inity.tickenity.domain.schedule.repository;

import com.inity.tickenity.domain.common.repository.BaseRepository;
import com.inity.tickenity.domain.schedule.entity.Schedule;

import java.util.List;

public interface ScheduleRepository extends BaseRepository<Schedule, Long> {
    List<Schedule> findAllByConcertId(Long concertId);
}
