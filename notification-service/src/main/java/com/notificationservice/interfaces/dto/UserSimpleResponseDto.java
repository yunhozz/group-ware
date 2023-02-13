package com.notificationservice.interfaces.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserSimpleResponseDto {

    private String userId;
    private String name;
    private String imageUrl;
}