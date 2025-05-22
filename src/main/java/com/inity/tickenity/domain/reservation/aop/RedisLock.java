package com.inity.tickenity.domain.reservation.aop;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RedisLock {
	String keyPrefix();              // 락 키의 접두사
	long waitTime() default 30;      // 락 대기 시간 (초)
	long leaseTime() default 30;     // 락 점유 시간 (초)
}