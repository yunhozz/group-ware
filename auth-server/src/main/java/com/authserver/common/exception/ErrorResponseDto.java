package com.authserver.common.exception;

import com.authserver.common.enums.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponseDto {

    private LocalDateTime timestamp;
    private Integer status;
    private String code;
    private String message;
    private List<NotValidResponseDto> validation;

    public ErrorResponseDto(ErrorCode errorCode) {
        timestamp = LocalDateTime.now();
        status = errorCode.getStatus();
        code = errorCode.getCode();
        message = errorCode.getMessage();
    }

    public ErrorResponseDto(ErrorCode errorCode, List<NotValidResponseDto> validation) {
        this.timestamp = LocalDateTime.now();
        this.status = errorCode.getStatus();
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
        this.validation = validation;
    }
}