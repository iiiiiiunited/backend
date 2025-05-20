package com.inity.tickenity.domain.schedule.controller;

import com.inity.tickenity.domain.schedule.dto.response.SeatResponseDto;
import com.inity.tickenity.domain.schedule.service.ScheduleSeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/concerts/{concertId}/schedules")
@RequiredArgsConstructor
public class ScheduleSeatController {

    private final ScheduleSeatService scheduleSeatService;

    @GetMapping("/{scheduleId}/seats")
    public ResponseEntity<?> getSeatList(
            @PathVariable Long concertId,
            @PathVariable Long scheduleId) {

        List<SeatResponseDto> seatList = scheduleSeatService.getSeatsForSchedule(concertId, scheduleId);
        return ResponseEntity.ok(Map.of(
                "scheduleId", scheduleId,
                "seats", seatList
        ));
    }
}
