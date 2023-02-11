package com.authserver.dto.response;

import com.authserver.common.enums.Provider;
import com.authserver.common.enums.Role;
import com.authserver.persistence.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {

    private Long id;
    private String userId;
    private String email;
    private String password;
    private String name;
    private String imageUrl;
    private Provider provider;
    private Role role;

    public UserResponseDto(User user) {
        id = user.getId();
        userId = user.getUserId();
        email = user.getEmail();
        password = user.getPassword();
        name = user.getName();
        imageUrl = user.getImageUrl();
        provider = user.getProvider();
        role = user.getRole();
    }
}