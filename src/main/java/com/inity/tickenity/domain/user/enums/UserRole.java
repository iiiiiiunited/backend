package com.inity.tickenity.domain.user.enums;

import com.inity.tickenity.global.exception.BusinessException;
import com.inity.tickenity.global.response.ResultCode;

import java.util.Arrays;

public enum UserRole {
    USER, ADMIN;

    public static UserRole of(String role) {
        return Arrays.stream(UserRole.values())
                .filter(r -> r.name().equalsIgnoreCase(role))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ResultCode.VALID_FAIL, "유효하지 않은 UerRole"));
    }

}
