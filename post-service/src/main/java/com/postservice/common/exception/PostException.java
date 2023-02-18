package com.postservice.common.exception;

import lombok.Getter;

@Getter
public class PostException extends RuntimeException {

    private final ErrorCode errorCode;

    public PostException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}