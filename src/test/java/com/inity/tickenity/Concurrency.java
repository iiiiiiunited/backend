package com.inity.tickenity;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.inity.tickenity.domain.venue.service.VenueService;

@SpringBootTest
public class Concurrency {

	@Autowired
	private VenueService venueService;


	@Test
	void increaseWithLettuceAndRetry() throws InterruptedException {
		ExecutorService executor = Executors.newFixedThreadPool(100);
		CountDownLatch latch = new CountDownLatch(100);
		for (int i = 0; i < 100; i++) {
			executor.submit(() -> {
				try {
					venueService.increaseCapacityWithRetryAndRedisLock(); // 또는 Lock 있는 버전
				} finally {
					latch.countDown();
				}
			});
		}
		latch.await();
		venueService.printCount();
	}
	@Test
	void increaseWithLettuce() throws InterruptedException {
		ExecutorService executor = Executors.newFixedThreadPool(100);
		CountDownLatch latch = new CountDownLatch(100);
		for (int i = 0; i < 100; i++) {
			executor.submit(() -> {
				try {
					venueService.increaseCapacityWithRedisLock(); // 또는 Lock 있는 버전
				} finally {
					latch.countDown();
				}
			});
		}
		latch.await();
		venueService.printCount();
	}
	@Test
	void increaseCapacityWithNothing() throws InterruptedException {
		ExecutorService executor = Executors.newFixedThreadPool(100);
		CountDownLatch latch = new CountDownLatch(100);
		for (int i = 0; i < 100; i++) {
			executor.submit(() -> {
				try {
					venueService.increaseCapacityWithNothing(); // 또는 Lock 있는 버전
				} finally {
					latch.countDown();
				}
			});
		}
		latch.await();
		venueService.printCount();
	}
	@Test
	void concurrencyTestUsingLock() throws InterruptedException {
		ExecutorService executor = Executors.newFixedThreadPool(100);
		CountDownLatch latch = new CountDownLatch(100);
		for (int i = 0; i < 100; i++) {
			executor.submit(() -> {
				try {
					venueService.increaseCapcityUsingRedisson();
				} finally {
					latch.countDown();
				}
			});
		}
		latch.await();
		venueService.printCount();
	}

	@Test
	void increaseCapcityUsingRedissonWithRetry() throws InterruptedException {
		ExecutorService executor = Executors.newFixedThreadPool(100);
		CountDownLatch latch = new CountDownLatch(100);
		for (int i = 0; i < 100; i++) {
			executor.submit(() -> {
				try {
					venueService.increaseCapcityUsingRedissonWithRetry();
				} finally {
					latch.countDown();
				}
			});
		}
		latch.await();
		venueService.printCount();
	}
}
