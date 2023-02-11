package com.authserver.common.exception;

import com.authserver.common.enums.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponseDto {

    private LocalDateTime timestamp;
    private Integer status;
    private String code;
    private String message;

    public ErrorResponseDto(ErrorCode errorCode) {
        timestamp = LocalDateTime.now();
        status = errorCode.getStatus();
        code = errorCode.getCode();
        message = errorCode.getMessage();
    }
}