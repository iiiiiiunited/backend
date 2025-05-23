package com.inity.tickenity.global.lock;

import com.inity.tickenity.global.exception.BusinessException;
import com.inity.tickenity.global.response.ResultCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.lang.reflect.Method;
import java.util.UUID;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
@Order(1)
public class LettuceLockAspect {

    private final LockService lockService;
    private final SpelExpressionParser parser = new SpelExpressionParser();

    @Around("@annotation(redisLock)")
    public Object applyRedisLock(ProceedingJoinPoint joinPoint, LettuceLock redisLock) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();

        EvaluationContext context = new StandardEvaluationContext();
        String[] paramNames = methodSignature.getParameterNames();
        Object[] args = joinPoint.getArgs();
        for (int i = 0; i < paramNames.length; i++) {
            context.setVariable(paramNames[i], args[i]);
        }

        String key = parser.parseExpression(redisLock.key()).getValue(context, String.class);
        String uuid = UUID.randomUUID().toString();

        log.info("[🔒 RedisLock] 락 획득 시도 - key: {}, uuid: {}", key, uuid);

        boolean locked = lockService.tryLock(key, uuid, redisLock.timeout());
        if (!locked) {
            log.warn("[⚠️ RedisLock] 락 획득 실패 - key: {}", key);
            throw new BusinessException(ResultCode.LOCK_FAIL, "락 획득 실패");
        }

        log.info("[✅ RedisLock] 락 획득 성공 - key: {}, uuid: {}", key, uuid);

        boolean unlockRequired = true;

        try {
            Object result = joinPoint.proceed();

            if (TransactionSynchronizationManager.isSynchronizationActive()) {
                unlockRequired = false; // unlock은 afterCommit에서 수행 예정
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        lockService.unlock(key, uuid);
                        log.info("[🔓 RedisLock] 커밋 이후 락 해제 완료 - key: {}, uuid: {}", key, uuid);
                    }
                });
            } else {
                // 트랜잭션이 없는 경우에는 즉시 해제하지 않고 unlockRequired=true 유지
                log.warn("[RedisLock] 트랜잭션 비활성 상태에서 커밋 후 해제 불가 - key: {}, uuid: {}", key, uuid);
            }

            return result;

        } catch (Exception e) {
            // 예외 발생 시 무조건 해제
            lockService.unlock(key, uuid);
            log.error("[🔥 RedisLock] 예외 발생 - 락 즉시 해제 - key: {}, uuid: {}, 메시지: {}", key, uuid, e.getMessage(), e);
            throw e;

        } finally {
            // 트랜잭션이 없고 예외도 없는 경우, 직접 해제
            if (unlockRequired && !TransactionSynchronizationManager.isSynchronizationActive()) {
                lockService.unlock(key, uuid);
                log.warn("[RedisLock] 트랜잭션 없이 정상 종료된 메서드의 락 수동 해제 - key: {}, uuid: {}", key, uuid);
            }
        }
    }
}
