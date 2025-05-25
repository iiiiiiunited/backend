package com.inity.tickenity.domain.redisRock.service;

import com.inity.tickenity.domain.redisRock.repository.LockRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class LockService {
    private final LockRedisRepository lockRedisRepository;

    public boolean lock(String key,
                     String value) throws InterruptedException {
        long start = System.currentTimeMillis();
        while (!lockRedisRepository.lock(key, value)) {
            if (System.currentTimeMillis() - start > 3000) {
                return false;
            }
            Thread.sleep(100);
        }
        return true;
    }

    public void unlock(String key) {
        lockRedisRepository.unlock(key);
    }
}
