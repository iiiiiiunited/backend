package com.inity.tickenity.domain.schedule.repository;

import com.inity.tickenity.domain.common.repository.BaseRepository;
import com.inity.tickenity.domain.schedule.entity.Schedule;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ScheduleRepository extends BaseRepository<Schedule, Long> {
    List<Schedule> findAllByConcertId(Long concertId);

    @Query("select sc from Schedule sc join fetch Seat s on sc.id = s.schedule.id where s.id = :seatId")
    Schedule findBySeatId(@Param("seatId") Long seatId);
}
