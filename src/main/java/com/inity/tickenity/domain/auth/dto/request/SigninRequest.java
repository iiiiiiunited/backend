package com.inity.tickenity.domain.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SigninRequest(
    @NotBlank @Email String email,
    @NotBlank String password
) {

}
