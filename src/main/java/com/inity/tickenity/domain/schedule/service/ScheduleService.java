package com.inity.tickenity.domain.schedule.service;

import com.inity.tickenity.domain.schedule.dto.response.ScheduleResponseDto;
import com.inity.tickenity.domain.schedule.entity.Schedule;
import com.inity.tickenity.domain.schedule.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;

    public List<ScheduleResponseDto> getSchedulesByConcert(Long concertId) {
        List<Schedule> schedules = scheduleRepository.findAllByConcertId(concertId);
        return schedules.stream()
                .map(s -> new ScheduleResponseDto(s.getId(), s.getStartTime(), s.getEndTime()))
                .toList();
    }


}
