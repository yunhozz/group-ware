package com.authserver.dto.response;

import com.authserver.common.enums.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Comparator;
import java.util.Set;

@Getter
@NoArgsConstructor
public class UserBasicResponseDto {

    private String userId;
    private String name;
    private String imageUrl;
    private String auth;

    public UserBasicResponseDto(String userId, String name, String imageUrl, Set<Role> roles) {
        this.userId = userId;
        this.name = name;
        this.imageUrl = imageUrl;
        this.auth = roles.stream()
                .sorted(Comparator.comparingInt(Role::getOrder))
                .map(Role::getDesc)
                .findFirst()
                .orElse("분류되지 않은 사용자");
    }
}