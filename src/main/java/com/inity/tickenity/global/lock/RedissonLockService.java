package com.inity.tickenity.global.lock;

import com.inity.tickenity.global.exception.BusinessException;
import com.inity.tickenity.global.response.ResultCode;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedissonLockService {

    private final RedissonClient redissonClient;

    public <T> T executeWithLock(String key, long waitTime, long leaseTime, Callable<T> task) {
        RLock lock = redissonClient.getLock(key);
        boolean isLocked = false;

        try {
            isLocked = lock.tryLock(waitTime, leaseTime, TimeUnit.MILLISECONDS);
            if (!isLocked) {
                throw new BusinessException(ResultCode.LOCK_FAIL, "Redisson 락 획득 실패");
            }
            return task.call();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException(ResultCode.LOCK_FAIL, "락 인터럽트 예외");
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (isLocked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}


