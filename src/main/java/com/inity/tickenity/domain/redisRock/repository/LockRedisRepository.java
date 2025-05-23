package com.inity.tickenity.domain.redisRock.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@RequiredArgsConstructor
@Repository
public class LockRedisRepository {
    private final RedisTemplate<String, String> redisTemplate;

    public Boolean lock(Object key, Object value) {
        return redisTemplate
                .opsForValue()
                .setIfAbsent(key.toString(), value.toString(), Duration.ofMillis(3000)); // SETNX(Set if Not Exists)의 기능
    }

    public Boolean unlock(Object key) {
        return redisTemplate.delete(key.toString());
    }
}
