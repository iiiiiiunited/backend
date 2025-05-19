package com.inity.tickenity.domain.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SignupRequest(
        @NotBlank @Email String email,
        @NotBlank String password,
        @NotBlank String nickname,
        @NotBlank String phone,
        @NotBlank String role
) {

}
