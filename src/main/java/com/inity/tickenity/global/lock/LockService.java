package com.inity.tickenity.global.lock;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class LockService {

    private final LockRedisRepository lockRedisRepository;

    public boolean tryLock(String key, String uuid, long millis) {
        return lockRedisRepository.lock(key, uuid, Duration.ofMillis(millis));
    }

    public void unlock(String key, String uuid) {
        lockRedisRepository.unlock(key, uuid);
    }

}
