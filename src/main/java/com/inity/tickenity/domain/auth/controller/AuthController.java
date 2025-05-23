package com.inity.tickenity.domain.auth.controller;

import com.inity.tickenity.domain.auth.dto.request.SigninRequest;
import com.inity.tickenity.domain.auth.dto.request.SignupRequest;
import com.inity.tickenity.domain.auth.dto.response.SigninResponse;
import com.inity.tickenity.domain.auth.dto.response.SignupResponse;
import com.inity.tickenity.domain.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/auth/signup")
    public SignupResponse signup(@Valid @RequestBody SignupRequest signupRequest) {
        return authService.signup(signupRequest);
    }

    @PostMapping("/auth/signin")
    public SigninResponse signin(@Valid @RequestBody SigninRequest signinRequest) {
        return authService.signin(signinRequest);
    }

}
