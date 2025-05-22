package com.inity.tickenity.global.lock;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class LockRedisRepository {

    private final StringRedisTemplate redisTemplate;

    public Boolean lock(String key, String value, Duration timeout) {
        return redisTemplate
                .opsForValue()
                .setIfAbsent(key, value, timeout); // NX 옵션 + TTL 부여
    }

    public void unlock(String key, String value) {
        String currentValue = redisTemplate.opsForValue().get(key);
        if (value.equals(currentValue)) {
            redisTemplate.delete(key); // 자신이 Lock을 가지고 있을 때만 해제
        }
    }
}
