package com.inity.tickenity.domain.redisRock.aop;

import com.inity.tickenity.domain.reservation.dto.reqeust.ReservationCreateRequestDto;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LettuceLock {
    String userId();
    String scheduleId();
    String seatInformationId();
}
