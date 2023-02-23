package com.authserver.dto.response;

import com.authserver.common.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserDataResponseDto {

    private Long id;
    private String userId;
    private String email;
    private String name;
    private String provider;
    private Set<Role> roles;
}