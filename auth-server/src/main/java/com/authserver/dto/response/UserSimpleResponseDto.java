package com.authserver.dto.response;

import com.authserver.common.enums.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Comparator;
import java.util.Set;

@Getter
@NoArgsConstructor
public class UserSimpleResponseDto {

    private String userId;
    private String auth;

    public UserSimpleResponseDto(String userId, Set<Role> roles) {
        this.userId = userId;
        this.auth = roles.stream()
                .sorted(Comparator.comparingInt(Role::getOrder))
                .map(Role::getDesc)
                .findFirst()
                .orElse("분류되지 않은 사용자");
    }
}