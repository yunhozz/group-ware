package com.authserver.dto.response;

import com.authserver.persistence.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponseDto {

    private String email;
    private String name;
    private String imageUrl;
    private String provider;
    private String auth;

    public UserProfileResponseDto(User user) {
        email = user.getEmail();
        name = user.getName();
        imageUrl = user.getImageUrl();
        provider = user.getProvider().getDesc();
        auth = user.getRole().getAuthority();
    }
}