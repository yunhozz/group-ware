package com.teamservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserBasicResponseDto {

    private String userId;
    private String name;
    private String imageUrl;
    private String auth;
}