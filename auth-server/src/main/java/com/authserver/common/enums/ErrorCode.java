package com.authserver.common.enums;

import lombok.Getter;

@Getter
public enum ErrorCode {

    INTER_SERVER_ERROR(500, "A-001", "서버 에러가 발생했습니다."),
    EMAIL_DUPLICATED(400, "A-002", "중복되는 이메일이 존재합니다."),
    EMAIL_NOT_FOUND(400, "A-003", "해당 이메일이 존재하지 않습니다."),
    PASSWORD_DIFFERENT(400, "A-004", "비밀번호가 일치하지 않습니다."),
    REFRESH_TOKEN_DIFFERENT(400, "A-005", "재발급 토큰 값이 다릅니다."),
    USER_NOT_FOUND(404, "A-006", "해당 유저를 찾을 수 없습니다."),
    REFRESH_TOKEN_NOT_FOUND(404, "A-007", "재발급 토큰이 만료되었거나 찾을 수 없습니다."),

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