package com.authserver.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserSimpleResponseDto {

    private String userId;
    private String name;
    private String imageUrl;

    @QueryProjection
    public UserSimpleResponseDto(String userId, String name, String imageUrl) {
        this.userId = userId;
        this.name = name;
        this.imageUrl = imageUrl;
    }
}