package com.mailservice.common.exception.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NotValidResponseDto {

    private String field;
    private Object rejectedValue;
    private String message;
}