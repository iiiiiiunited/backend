package com.inity.tickenity.domain.redisRock.aop;

import com.inity.tickenity.domain.redisRock.repository.LockRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.concurrent.TimeUnit;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RedisAspect {

    private final LockRedisRepository lockRedisRepository;
    private final RedissonClient redissonClient;
    private final PlatformTransactionManager transactionManager;

    @Around("@annotation(lettuceLock)")
    public Object lettuceLock(ProceedingJoinPoint joinPoint, LettuceLock lettuceLock) throws Throwable {
        String key = "lock:" + lettuceLock.scheduleId() + lettuceLock.seatInformationId();
        boolean locked = false;
        try {
            long start = System.currentTimeMillis();
            while (!(locked = lockRedisRepository.lock(key, lettuceLock.userId()))) {
                if (System.currentTimeMillis() - start > 3000) {
                    throw new IllegalStateException("락 획득 실패: key = " + key);
                }
                Thread.sleep(100);
            }

            log.info("락 획득 성공: key = {}, userId = {}", key, lettuceLock.userId());

            TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
            transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

            return transactionTemplate.execute(status -> {
                try {
                    return joinPoint.proceed();
                } catch (Throwable throwable) {
                    status.setRollbackOnly();
                    throw new RuntimeException(throwable);
                }
            });

        } finally {
            if (locked) {
                try {
                    lockRedisRepository.unlock(key);
                    log.info("락 해제: key = {}", key);
                } catch (Exception e) {
                    log.error("락 해제 실패: key = {}, error = {}", key, e.getMessage());
                }
            }
        }
    }

    @Around("@annotation(redissonLock)")
    public Object redissonLock(ProceedingJoinPoint joinPoint, RedissonLock redissonLock) throws Throwable {
        String key = "lock:" + redissonLock.scheduleId() + redissonLock.seatInformationId();
        long waitTime = 5; // 락 대기 시간
        long leaseTime = 10; // 점유 시간

        RLock lock = redissonClient.getLock(key);

        boolean isLocked = false;
        try {
            isLocked = lock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS);
            if (!isLocked) {
                throw new IllegalStateException("락 획득 실패: key = " + key);
            }

            log.info("락 획득 성공: key = {}", key);

            TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
            transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

            return transactionTemplate.execute(status -> {
                try {
                    return joinPoint.proceed(); // 비즈니스 로직 실행
                } catch (Throwable throwable) {
                    // 트랜잭션 롤백을 명시적으로 설정
                    status.setRollbackOnly();
                    throw new RuntimeException(throwable);
                }
            });
        } finally {
            if (isLocked && lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.info("락 해제: key = {}", key);
            }
        }
    }
}