package com.teamservice.common.exception.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.teamservice.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponseDto {

    private LocalDateTime timestamp;
    private Integer status;
    private String code;
    private String message;
    private List<NotValidResponseDto> validation;

    public ErrorResponseDto(ErrorCode errorCode) {
        this.timestamp = LocalDateTime.now();
        this.status = errorCode.getStatus();
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

    public ErrorResponseDto(ErrorCode errorCode, List<NotValidResponseDto> validation) {
        this.timestamp = LocalDateTime.now();
        this.status = errorCode.getStatus();
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
        this.validation = validation;
    }
}