package com.inity.tickenity.domain.schedule.repository;

import com.inity.tickenity.domain.common.repository.BaseRepository;
import com.inity.tickenity.domain.schedule.entity.Schedule;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleRepository extends BaseRepository<Schedule, Long> {
    List<Schedule> findByConcertId(Long concertId);
}
