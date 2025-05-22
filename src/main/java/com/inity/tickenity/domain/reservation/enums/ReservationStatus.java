package com.inity.tickenity.domain.reservation.enums;

public enum ReservationStatus {
    RESERVED,           // 예매 완료
    PENDING,            // 예매 대기
    CANCELLED,          // 예매 취소
    EXPIRED,            // 예매 유효시간 초과로 자동 취소
    USED,               // 사용 완료
    NO_SHOW             // 예매 후 미사용
}
