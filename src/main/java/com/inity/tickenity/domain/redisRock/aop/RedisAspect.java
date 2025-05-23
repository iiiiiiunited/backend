package com.inity.tickenity.domain.redisRock.aop;

import com.inity.tickenity.domain.redisRock.repository.LockRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
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

        // 메서드 시그니처
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String[] parameterNames = methodSignature.getParameterNames(); // 파라미터 이름 배열
        Object[] args = joinPoint.getArgs(); // 실제 파라미터 값 배열

        // SpEL 파서와 컨텍스트 준비
        SpelExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();

        // 파라미터 이름과 값 context에 세팅
        for (int i = 0; i < parameterNames.length; i++) {
            context.setVariable(parameterNames[i], args[i]);
        }

        // SpEL 평가
        String seatId = parser.parseExpression(lettuceLock.seatId()).getValue(context, String.class);
        String userId = parser.parseExpression(lettuceLock.userId()).getValue(context, String.class);

        String key = "lettuceLock:" + lettuceLock.seatId();
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
        // 메서드 시그니처
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String[] parameterNames = methodSignature.getParameterNames(); // 파라미터 이름 배열
        Object[] args = joinPoint.getArgs(); // 실제 파라미터 값 배열

        // SpEL 파서와 컨텍스트 준비
        SpelExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();

        // 파라미터 이름과 값 context에 세팅
        for (int i = 0; i < parameterNames.length; i++) {
            context.setVariable(parameterNames[i], args[i]);
        }

        // SpEL 평가
        String seatId = parser.parseExpression(redissonLock.seatId()).getValue(context, String.class);

        String key = "redissonLock:" + seatId;
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