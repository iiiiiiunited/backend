package com.inity.tickenity;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class TickenityApplicationTests {

	@MockitoBean
	private RedisTemplate<String, Object> redisTemplate;

	@Test
	void contextLoads() {
	}

}
