package com.inity.tickenity.domain.common.dto;

import com.inity.tickenity.domain.user.enums.UserRole;

public record AuthUser(
        Long id,
        String email,
        UserRole userRole
) {

}
