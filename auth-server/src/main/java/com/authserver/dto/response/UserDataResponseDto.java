package com.authserver.dto.response;

import com.authserver.common.enums.Role;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Getter
@NoArgsConstructor
public class UserDataResponseDto {

    private Long id;
    private String userId;
    private String email;
    private String name;
    private String provider;
    private Set<Role> roles;

    @QueryProjection
    public UserDataResponseDto(Long id, String userId, String email, String name, String provider, Set<Role> roles) {
        this.id = id;
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.provider = provider;
        this.roles = roles;
    }
}