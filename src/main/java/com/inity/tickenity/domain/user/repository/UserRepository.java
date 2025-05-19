package com.inity.tickenity.domain.user.repository;

import com.inity.tickenity.domain.common.repository.BaseRepository;
import com.inity.tickenity.domain.user.entity.User;

import java.util.Optional;

public interface UserRepository extends BaseRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

}
