package com.inity.tickenity.global.lock;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LettuceLock {
    String key();             // key의 SpEL 표현식
    long timeout() default 3000; // 락 점유 시간 (ms)
}
