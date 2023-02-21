package com.authserver.dto.response;

import com.authserver.common.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserSimpleResponseDto {

    private String userId;
    private Set<Role> roles;
}