package com.inity.tickenity.domain.seat.enums;

public enum SeatStatus {
    AVAILABLE,       // 사용 가능
    RESERVED,        // 예약됨
    OCCUPIED,        // 점유됨 (사용 중)
    BLOCKED,         // 차단됨 (일시적으로 사용 불가)
    MAINTENANCE,     // 점검 중
    UNAVAILABLE      // 기타 사유로 사용 불가
}
