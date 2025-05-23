package com.inity.tickenity.global.lock;

import com.inity.tickenity.global.exception.BusinessException;
import com.inity.tickenity.global.response.ResultCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.core.annotation.Order;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
@Order(1)
public class RedissonLockAspect {

    private final RedissonClient redissonClient;
    private final SpelExpressionParser parser = new SpelExpressionParser();

    @Around("@annotation(redissonLock)")
    public Object applyRedissonLock(ProceedingJoinPoint joinPoint, RedissonLock redissonLock) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        EvaluationContext context = new StandardEvaluationContext();
        String[] paramNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();
        for (int i = 0; i < paramNames.length; i++) {
            context.setVariable(paramNames[i], args[i]);
        }

        String key = parser.parseExpression(redissonLock.key()).getValue(context, String.class);
        RLock lock = redissonClient.getLock(key);

        boolean isLocked = false;

        try {
            log.info("[🔒 RedissonLock] 락 획득 시도 - key: {}", key);
            isLocked = lock.tryLock(redissonLock.waitTime(), redissonLock.leaseTime(), TimeUnit.MILLISECONDS);

            if (!isLocked) {
                log.warn("[⚠️ RedissonLock] 락 획득 실패 - key: {}", key);
                throw new BusinessException(ResultCode.LOCK_FAIL, "Redisson 락 획득 실패");
            }

            log.info("[✅ RedissonLock] 락 획득 성공 - key: {}", key);

            Object result = joinPoint.proceed();

            if (TransactionSynchronizationManager.isSynchronizationActive()) {
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        if (lock.isHeldByCurrentThread()) {
                            lock.unlock();
                            log.info("[🔓 RedissonLock] 커밋 이후 락 해제 완료 - key: {}", key);
                        }
                    }
                });
            } else {
                // 트랜잭션이 없는 경우 즉시 해제
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                    log.warn("[RedissonLock] 트랜잭션 없이 락 즉시 해제 - key: {}", key);
                }
            }

            return result;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException(ResultCode.LOCK_FAIL, "Redisson 락 인터럽트 예외");
        } catch (Exception e) {
            if (isLocked && lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.error("[🔥 RedissonLock] 예외 발생 - 락 즉시 해제 - key: {}, 메시지: {}", key, e.getMessage(), e);
            }
            throw e;
        }
    }
}
