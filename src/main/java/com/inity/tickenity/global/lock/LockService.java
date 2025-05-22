package com.inity.tickenity.global.lock;

import com.inity.tickenity.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.inity.tickenity.global.response.ResultCode;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LockService {

    private final LockRedisRepository lockRedisRepository;

    public void executeWithLock(String key, Runnable task) {
        String uuid = UUID.randomUUID().toString();
        boolean isLocked = lockRedisRepository.lock(key, uuid, Duration.ofMillis(3000));

        if (!isLocked) {
            throw new BusinessException(ResultCode.LOCK_FAIL, "락 획득 실패");
        }

        try {
            task.run();
        } finally {
            lockRedisRepository.unlock(key, uuid);
        }
    }

    public boolean tryLock(String key, String uuid, long millis) {
        return lockRedisRepository.lock(key, uuid, Duration.ofMillis(millis));
    }

    public void unlock(String key, String uuid) {
        lockRedisRepository.unlock(key, uuid);
    }

}
