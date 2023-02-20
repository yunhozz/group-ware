package com.teamservice.common.exception;

import lombok.Getter;

@Getter
public class TeamException extends RuntimeException {

    private final ErrorCode errorCode;

    public TeamException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}