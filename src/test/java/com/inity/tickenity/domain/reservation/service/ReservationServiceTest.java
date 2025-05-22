package com.inity.tickenity.domain.reservation.service;

import com.inity.tickenity.domain.concert.entity.Concert;
import com.inity.tickenity.domain.concert.enums.Genre;
import com.inity.tickenity.domain.concert.repository.ConcertRepository;
import com.inity.tickenity.domain.reservation.dto.reqeust.ReservationCreateRequestDto;
import com.inity.tickenity.domain.schedule.entity.Schedule;
import com.inity.tickenity.domain.schedule.repository.ScheduleRepository;
import com.inity.tickenity.domain.seat.entity.SeatInformation;
import com.inity.tickenity.domain.seat.enums.SeatGradeType;
import com.inity.tickenity.domain.seat.repository.SeatInformationRepository;
import com.inity.tickenity.domain.user.entity.User;
import com.inity.tickenity.domain.user.enums.UserRole;
import com.inity.tickenity.domain.user.repository.UserRepository;
import com.inity.tickenity.domain.venue.entity.Venue;
import com.inity.tickenity.domain.venue.repository.VenueRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

//@Disabled("임시로 비활성화")
@SpringBootTest
class ReservationServiceTest {

    @Autowired
    private ReservationService reservationService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SeatInformationRepository seatInformationRepository;
    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private VenueRepository venueRepository;
    @Autowired
    private ConcertRepository concertRepository;

    @BeforeEach
    void setUp() {
        // 1. 장소 저장
        Venue venue = venueRepository.save(new Venue("address", "name", 45, "tt"));
        // 2. 콘서트 저장
        Concert concert = concertRepository.save(new Concert("test", "전체이용가", 23, Genre.CLASSICAL, "t", "url"));
        // 3. 일정 저장
        scheduleRepository.save(new Schedule(concert, LocalDateTime.now(), LocalDateTime.now()));
        // 4. 좌석 정보 저장
        seatInformationRepository.save(new SeatInformation(venue, SeatGradeType.VIP, "A1"));
        // 5. 유저 정보 저장
        IntStream.range(0, 50).forEach(i -> {
            userRepository.save(new User(i + "test@test.com", "pwd", "test", "000-0000-0000", UserRole.USER));
        });
    }

//    @Disabled("임시로 비활성화")
    @DisplayName("동일 좌석에 대한 동시 예약 시 하나만 성공해야 한다 - Callable")
    @Test
    void shouldAllowOnlyOneReservationWhenMultipleUsersReserveSameSeatConcurrently() throws InterruptedException {
        System.out.println("\n\n\n\n[createReservation Test]");
        // ---------------------------
        // given: 예약 요청 정보 및 스레드풀 설정
        // ---------------------------
        ReservationCreateRequestDto reservationCreateRequestDto = new ReservationCreateRequestDto(1L, 1L);

        ExecutorService executor = Executors.newFixedThreadPool(10);

        List<Callable<Void>> tasks = IntStream.range(0, 1000)
                .mapToObj(i -> (Callable<Void>) () -> {
                    reservationService.createReservation((long) i + 1, reservationCreateRequestDto);
                    return null;
                }).collect(Collectors.toList());

        // ---------------------------
        // when: 1000명의 사용자가 동시에 예약 요청
        // ---------------------------
        executor.invokeAll(tasks);
        executor.shutdown();

        // ---------------------------
        // then: 성공한 예약은 정확히 1건이어야 함
        // 지금은 실패
        // ---------------------------
        Assertions.assertEquals(1L, reservationService.countReservation());
    }

    @DisplayName("동일 좌석에 대한 동시 예약 시 하나만 성공해야 한다 - CountDownLatch")
    @Test
    void shouldAllowOnlyOneReservationWhenMultipleUsersReserveSameSeatConcurrentlyWithCountDownLatch() throws InterruptedException {
        System.out.println("\n\n\n\n[createReservation Test with CountDownLatch]");

        // ---------------------------
        // given: 예약 요청 정보 및 스레드풀 설정
        // ---------------------------
        ReservationCreateRequestDto reservationCreateRequestDto = new ReservationCreateRequestDto(1L, 1L);

        ExecutorService executor = Executors.newFixedThreadPool(10);
        int tryCount = 1000;

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();
        CountDownLatch latch = new CountDownLatch(tryCount);

        // ---------------------------
        // when: 100명의 사용자가 동시에 예약 요청
        // ---------------------------
        for (int i = 0; i < tryCount; i++) {
            int finalI = i;
            executor.submit(() -> {
                try {
                    reservationService.createReservation(1L + finalI, reservationCreateRequestDto);
                    successCount.getAndIncrement();
                } catch (Exception e) {
                    System.out.println("[error]  : " + e.getMessage());
                    failCount.getAndIncrement();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        // ---------------------------
        // then: 성공한 예약은 정확히 1건이어야 함
        // 지금은 실패
        // ---------------------------
        // 실패 나는 게 정상 입니다.
        Assertions.assertEquals(1L, reservationService.countReservation());
    }

//    @Disabled("임시로 비활성화")
    @DisplayName("동일 좌석에 대한 동시 예약 시 하나만 성공해야 한다 - With Redis Lettuce")
    @Test
    void shouldAllowOnlyOneReservationWhenMultipleUsersReserveSameSeatConcurrentlyWithLettuce() throws InterruptedException {
        System.out.println("\n\n\n\n[createReservation Test - Lettuce]");
        // ---------------------------
        // given: 예약 요청 정보 및 스레드풀 설정
        // ---------------------------
        ReservationCreateRequestDto reservationCreateRequestDto = new ReservationCreateRequestDto(1L, 1L);

        ExecutorService executor = Executors.newFixedThreadPool(10);
        int tryCount = 100;

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();
        CountDownLatch latch = new CountDownLatch(tryCount);

        // ---------------------------
        // when: 1000명의 사용자가 동시에 예약 요청
        // ---------------------------
        for (int i = 0; i < tryCount; i++) {
            int finalI = i;
            executor.submit(() -> {
                try {
                    reservationService.createReservationWithLettuce(1L + finalI, reservationCreateRequestDto);
                    successCount.getAndIncrement();
                } catch (Exception e) {
                    System.out.println("[error]  : " + e.getMessage());
                    failCount.getAndIncrement();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        // ---------------------------
        // then: 성공한 예약은 정확히 1건이어야 함
        // 지금은 실패
        // ---------------------------
        Assertions.assertEquals(1L, reservationService.countReservation());
        System.out.println("성공: " + successCount + "  | 실패" + failCount);
    }
}