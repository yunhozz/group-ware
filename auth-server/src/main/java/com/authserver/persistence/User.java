package com.authserver.persistence;

import com.authserver.common.enums.Provider;
import com.authserver.common.enums.Role;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;

    private String email;

    private String password;

    private String name;

    private String imageUrl;

    private Provider provider; // LOCAL, GOOGLE, KAKAO, NAVER

    @Enumerated(EnumType.STRING)
    private Set<Role> roles = new HashSet<>();

    @Builder
    private User(String userId, String email, String password, String name, String imageUrl, Provider provider, Set<Role> roles) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.name = name;
        this.imageUrl = imageUrl;
        this.provider = provider;
        this.roles = roles;
    }

    public User updateByProvider(String name, String imageUrl, Provider provider) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.provider = provider;
        return this;
    }
}