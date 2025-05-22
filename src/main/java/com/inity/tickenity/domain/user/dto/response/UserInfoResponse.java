package com.inity.tickenity.domain.user.dto.response;

import com.inity.tickenity.domain.user.enums.UserRole;

public record UserInfoResponse(
        Long userId,
        String email,
        String name,
        UserRole userRole
) {
}
