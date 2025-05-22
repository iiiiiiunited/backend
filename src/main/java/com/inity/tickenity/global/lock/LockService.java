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


    public boolean tryLock(String key, String uuid, long millis) {
        return lockRedisRepository.lock(key, uuid, Duration.ofMillis(millis));
    }

    public void unlock(String key, String uuid) {
        lockRedisRepository.unlock(key, uuid);
    }

}
