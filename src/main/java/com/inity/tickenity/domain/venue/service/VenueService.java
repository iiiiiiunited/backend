package com.inity.tickenity.domain.venue.service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inity.tickenity.domain.concert.entity.Concert;
import com.inity.tickenity.domain.concert.repository.ConcertRepository;
import com.inity.tickenity.domain.concertvenue.ConcertVenue;
import com.inity.tickenity.domain.venue.dto.CreateVenueRequestDto;
import com.inity.tickenity.domain.venue.dto.VenueResponseDto;
import com.inity.tickenity.domain.venue.entity.Venue;
import com.inity.tickenity.domain.venue.repository.VenueRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VenueService {
	private final VenueRepository venueRepository;
	private final ConcertRepository concertRepository;

	private final RedissonClient redissonClient;
	private final RedisTemplate<String, String> redisTemplate;

	private static final String LOCK_KEY = "counterLock";

	@Transactional
	public long createVenue(CreateVenueRequestDto req) {
		return venueRepository.save(req.fromDto()).getId();
	}

	@Transactional(readOnly = true)
	public List<VenueResponseDto> readVenueWithConcert(long concertId) {
		Concert concert = concertRepository.findById(concertId).orElseThrow();
		List<ConcertVenue> cvs = venueRepository.findAllByConcert(concert);
		List<VenueResponseDto> dtos = new ArrayList<>();
		for(ConcertVenue cv : cvs) {
			dtos.add(VenueResponseDto.toDto(cv.getVenue()));
		}
		return dtos;
	}

	@Transactional
	public void increaseCapacityWithNothing() {
		Venue venue = venueRepository.findById(3L).orElseThrow();
		venue.increaseCapacity();
		venueRepository.save(venue);
	}


	public void increaseCapcityUsingRedissonWithRetry() {
		RLock lock = redissonClient.getFairLock(LOCK_KEY);

		int retryCount = 10;
		int attempts = 0;

		while (attempts < retryCount) {
			boolean isLocked = false;
			try {
				isLocked = lock.tryLock(2, 10, TimeUnit.SECONDS); // 2초까지 기다리고, 10초 동안 점유
				if (isLocked) {
					Venue venue = venueRepository.findById(3L).orElseThrow();
					venue.increaseCapacity();
					venueRepository.save(venue);
					return; // 성공했으면 종료
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			} finally {
				if (isLocked && lock.isHeldByCurrentThread()) {
					lock.unlock();
				}
			}

			// 실패했으면 잠깐 쉬고 다시 시도
			attempts++;
			try {
				Thread.sleep(100); // 100ms 대기 후 재시도
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				break;
			}
		}
	}
	@Transactional
	public void increaseCapcityUsingRedisson() {
		RLock lock = redissonClient.getFairLock(LOCK_KEY);
		try {
			lock.tryLock(100, 100, TimeUnit.SECONDS);
			//락 대기 시간
			// 재시도
			Venue venue = venueRepository.findById(3L).orElseThrow();
			venue.increaseCapacity();
			venueRepository.save(venue);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		} finally {
			if (lock.isHeldByCurrentThread()) {
				lock.unlock();
			}
		}
	}

	@Transactional
	public void increaseCapacityWithRedisLock() {
		String lockValue = UUID.randomUUID().toString();
		Boolean isLocked = redisTemplate.opsForValue().setIfAbsent(LOCK_KEY, lockValue, Duration.ofMillis(5000L));

		if (Boolean.TRUE.equals(isLocked)) {
			try {
				Venue venue = venueRepository.findById(3L).orElseThrow();
				venue.increaseCapacity();
				venueRepository.save(venue);
			} finally {
				// 락 소유자만 락을 해제할 수 있도록 보장
				String currentValue = String.valueOf(redisTemplate.opsForValue().get(LOCK_KEY));
				if (lockValue.equals(currentValue)) {
					redisTemplate.delete(LOCK_KEY);
				}
			}
		} else {
			// 락 획득 실패 시 재시도 로직을 구현할 수 있음 (Optional)
		}
	}

	@Transactional
	public void increaseCapacityWithRetryAndRedisLock() {
		String lockValue = UUID.randomUUID().toString();
		int retry = 10;
		boolean locked = false;

		while (retry-- > 0) {
			Boolean isLocked = redisTemplate.opsForValue()
				.setIfAbsent(LOCK_KEY, lockValue, Duration.ofMillis(300));

			if (Boolean.TRUE.equals(isLocked)) {
				locked = true;
				break;
			}

			try {
				Thread.sleep(50); // 재시도 간격
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}

		if (!locked) return;

		try {
			Venue venue = venueRepository.findById(3L).orElseThrow();
			venue.increaseCapacity();
			venueRepository.save(venue);
		} finally {
			String currentValue = redisTemplate.opsForValue().get(LOCK_KEY);
			if (lockValue.equals(currentValue)) {
				redisTemplate.delete(LOCK_KEY);
			}
		}
	}

	public void printCount() {
		System.out.println("\n\n\n====================\n");
		System.out.println(venueRepository.findById(3L).orElseThrow().getCapacity());
		System.out.println("\n====================\n\n\n");
	}
}
