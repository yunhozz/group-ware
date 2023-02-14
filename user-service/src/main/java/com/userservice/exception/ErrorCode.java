package com.userservice.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    NOT_VALID(400, "U-001", "잘못된 요청입니다."),
    NOT_AUTHORIZED(401, "U-002", "해당 리소스에 대한 접근 권한이 없습니다.")

    ;

    private final int status;
    private final String code;
    private final String message;

    ErrorCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}