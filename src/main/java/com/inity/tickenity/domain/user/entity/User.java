package com.inity.tickenity.domain.user.entity;

import com.inity.tickenity.domain.common.entity.BaseTimeEntity;
import com.inity.tickenity.domain.user.enums.UserRole;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;

@Entity
@Table(name = "users")
@Getter
@SQLDelete(sql = "UPDATE users SET is_deleted = true WHERE id = ?")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole userRole;

    @Column(nullable = false)
    private boolean isDeleted = Boolean.FALSE;

    @Builder
    public User(String email, String password, String nickname, String phone, UserRole userRole) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.phone = phone;
        this.userRole = userRole;
    }
}
