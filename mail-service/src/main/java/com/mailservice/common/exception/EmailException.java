package com.mailservice.common.exception;

import lombok.Getter;

@Getter
public class EmailException extends RuntimeException {

    private final ErrorCode errorCode;

    public EmailException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}