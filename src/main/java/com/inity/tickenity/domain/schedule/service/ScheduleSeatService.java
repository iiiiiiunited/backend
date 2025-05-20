package com.inity.tickenity.domain.schedule.service;

import com.inity.tickenity.domain.reservation.repository.ReservationRepository;
import com.inity.tickenity.domain.schedule.dto.response.SeatResponseDto;
import com.inity.tickenity.domain.seat.entity.SeatGradePrice;
import com.inity.tickenity.domain.seat.entity.SeatInformation;
import com.inity.tickenity.domain.seat.enums.SeatGradeType;
import com.inity.tickenity.domain.seat.repository.SeatGradePriceRepository;
import com.inity.tickenity.domain.seat.repository.SeatInformationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleSeatService {

    private final SeatInformationRepository seatInformationRepository;
    private final SeatGradePriceRepository seatGradePriceRepository;
    private final ReservationRepository reservationRepository;

    public List<SeatResponseDto> getSeatsForSchedule(Long concertId, Long scheduleId) {

        // 1. 콘서트장에 존재하는 좌석 목록 조회
        List<SeatInformation> seats = seatInformationRepository.findByConcertId(concertId);

        // 2. 해당 콘서트의 등급별 가격 정보 조회
        Map<SeatGradeType, Integer> gradeToPrice = seatGradePriceRepository.findByConcertId(concertId).stream()
                .collect(Collectors.toMap(
                        SeatGradePrice::getGrade,
                        SeatGradePrice::getPrice
                ));

        // 3. 해당 일정에서 예약된 좌석 번호 목록 조회
        Set<String> reservedNumbers = reservationRepository.findReservedSeatNumbers(scheduleId);

        // 4. 응답 변환
        return seats.stream()
                .map(seat -> new SeatResponseDto(
                        seat.getNumber(),
                        seat.getGrade(),
                        gradeToPrice.get(seat.getGrade()),
                        reservedNumbers.contains(seat.getNumber())
                ))
                .collect(Collectors.toList());
    }
}
