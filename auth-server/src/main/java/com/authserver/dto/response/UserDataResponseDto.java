package com.authserver.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserDataResponseDto {

    private Long id;
    private String userId;
    private String email;
    private String name;
    private String provider;
    private String auth;

    @QueryProjection
    public UserDataResponseDto(Long id, String userId, String email, String name, String provider, String auth) {
        this.id = id;
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.provider = provider;
        this.auth = auth;
    }
}