package com.authserver.common.exception;

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