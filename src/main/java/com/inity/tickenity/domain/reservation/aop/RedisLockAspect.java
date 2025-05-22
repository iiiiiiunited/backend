package com.inity.tickenity.domain.reservation.aop;

import java.util.concurrent.TimeUnit;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import com.inity.tickenity.domain.reservation.dto.reqeust.ReservationCreateRequestDto;
import com.inity.tickenity.domain.venue.entity.Venue;
import com.inity.tickenity.global.exception.BusinessException;
import com.inity.tickenity.global.response.ResultCode;

import lombok.RequiredArgsConstructor;

@Component
@Aspect
@RequiredArgsConstructor
public class RedisLockAspect {
	private final RedissonClient redissonClient;

	@Around("@annotation(redisLock) && args(requestDto)")
	public Object around(ProceedingJoinPoint joinPoint, RedisLock redisLock, ReservationCreateRequestDto requestDto) throws Throwable {
		String key = buildKey(redisLock.keyPrefix(), requestDto);
		System.out.println(key);

		RLock lock = redissonClient.getLock(key);

		boolean isLocked = false;
		try {
			isLocked = lock.tryLock(redisLock.waitTime(), redisLock.leaseTime(), TimeUnit.SECONDS);
			System.out.println(isLocked);
			// if (!isLocked) {
			// 	throw new BusinessException(ResultCode.FAIL, "실패");
			// }
			if (isLocked) {
				return joinPoint.proceed();
			}
			throw new BusinessException(ResultCode.FAIL, "실패");

		} finally {
			if (isLocked && lock.isHeldByCurrentThread()) {
				lock.unlock();
			}
		}
	}

	private String buildKey(String prefix, ReservationCreateRequestDto requestDto) {
		return prefix + ":schedule:" + requestDto.scheduleId() + ":seat:" + requestDto.seatInformationId();
	}
}
