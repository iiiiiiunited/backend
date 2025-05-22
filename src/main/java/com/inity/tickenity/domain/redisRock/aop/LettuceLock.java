package com.inity.tickenity.domain.redisRock.aop;


import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LettuceLock {
    String userId();
    String scheduleId();
    String seatInformationId();
}
