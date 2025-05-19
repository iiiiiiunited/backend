package com.inity.tickenity.domain.schedule.controller;

import com.inity.tickenity.domain.schedule.dto.response.ScheduleResponseDto;
import com.inity.tickenity.domain.schedule.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/concerts")
public class ScheduleController {

    private final ScheduleService scheduleService;

    @GetMapping("/{concertId}/schedules")
    public List<ScheduleResponseDto> getSchedules(@PathVariable Long concertId) {
        return scheduleService.getSchedulesByConcert(concertId);
    }

}

