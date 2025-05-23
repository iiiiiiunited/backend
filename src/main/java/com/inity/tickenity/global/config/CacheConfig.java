package com.inity.tickenity.global.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.aspectj.lang.annotation.Around;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.support.CompositeCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@EnableCaching
@Configuration
public class CacheConfig {

    // 로컬 캐시 (Caffeine)
    @Bean("localCacheManager")
    public CaffeineCacheManager localCacheManager() {
        CaffeineCacheManager cm = new CaffeineCacheManager("concertTitlesLocal");
        cm.setCaffeine(
                Caffeine.newBuilder()
                        .expireAfterWrite(5, TimeUnit.MINUTES)   // 5분 캐시 유지
                        .maximumSize(500)                       // 최대 500개 키
        );
        return cm;
    }

    // Redis 캐시
    @Bean("redisCacheManager")
    public RedisCacheManager redisCacheManager(RedisConnectionFactory cf) {
        // key는 문자열, value는 JSON 직렬화
        RedisSerializationContext.SerializationPair<String> keySerializer =
                RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer());
        RedisSerializationContext.SerializationPair<Object> valueSerializer =
                RedisSerializationContext.SerializationPair.fromSerializer(
                        new GenericJackson2JsonRedisSerializer()
                );

        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(5))          // 필요에 따라 TTL 조정
                .serializeKeysWith(keySerializer)
                .serializeValuesWith(valueSerializer)
                .disableCachingNullValues();              // null 값 캐싱 제외

        return RedisCacheManager.builder(cf)
                .cacheDefaults(config)
                .build();
    }

    @Bean
    @Primary
    public CompositeCacheManager cacheManager(
            @Qualifier("localCacheManager") CacheManager local,
            @Qualifier("redisCacheManager") CacheManager redis) {
        CompositeCacheManager composite = new CompositeCacheManager(local, redis);
        composite.setFallbackToNoOpCache(false);
        return composite;
    }

}
