package com.inity.tickenity.domain.auth.service;

import com.inity.tickenity.domain.auth.dto.request.SigninRequest;
import com.inity.tickenity.domain.auth.dto.request.SignupRequest;
import com.inity.tickenity.domain.auth.dto.response.SigninResponse;
import com.inity.tickenity.domain.auth.dto.response.SignupResponse;
import com.inity.tickenity.domain.user.entity.User;
import com.inity.tickenity.domain.user.enums.UserRole;
import com.inity.tickenity.domain.user.repository.UserRepository;
import com.inity.tickenity.global.auth.JwtUtil;
import com.inity.tickenity.global.auth.PasswordEncoder;
import com.inity.tickenity.global.exception.BusinessException;
import com.inity.tickenity.global.response.ResultCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public SignupResponse signup(SignupRequest signupRequest) {

        if (userRepository.existsByEmail(signupRequest.email())) {
            throw new BusinessException(ResultCode.VALID_FAIL, "이미 존재하는 이메일입니다.");
        }

        String encodedPassword = passwordEncoder.encode(signupRequest.password());

        UserRole userRole = UserRole.of(signupRequest.role());

        User newUser = User.builder()
                .email(signupRequest.email())
                .password(encodedPassword)
                .nickname(signupRequest.nickname())
                .phone(signupRequest.phone())
                .userRole(userRole)
                .build();
        User savedUser = userRepository.save(newUser);

        String bearerToken = jwtUtil.createToken(savedUser.getId(), savedUser.getEmail(), savedUser.getNickname(), userRole);

        return new SignupResponse(bearerToken);
    }

    @Transactional(readOnly = true)
    public SigninResponse signin(SigninRequest signinRequest) {
        User user = userRepository.findByEmail(signinRequest.email()).orElseThrow(
                () -> new BusinessException(ResultCode.NOT_FOUND, "가입되지 않은 유저입니다."));

        if (!passwordEncoder.matches(signinRequest.password(), user.getPassword())) {
            throw new BusinessException(ResultCode.LOGIN_FAILED);
        }

        String bearerToken = jwtUtil.createToken(user.getId(), user.getEmail(), user.getNickname(), user.getUserRole());

        return new SigninResponse(bearerToken);
    }

}
