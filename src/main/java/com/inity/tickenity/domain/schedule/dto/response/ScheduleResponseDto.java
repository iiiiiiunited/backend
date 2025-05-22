package com.inity.tickenity.domain.schedule.dto.response;

import java.time.LocalDateTime;

public record ScheduleResponseDto(
        Long scheduleId,
        LocalDateTime startTime,
        LocalDateTime endTime
){}
