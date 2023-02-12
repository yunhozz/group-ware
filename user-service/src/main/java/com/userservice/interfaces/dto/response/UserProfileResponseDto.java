package com.userservice.interfaces.dto.response;

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
}