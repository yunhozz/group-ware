package com.authserver.common.enums;

import lombok.Getter;

@Getter
public enum ErrorCode {

    INTER_SERVER_ERROR(500, "A-001", "서버 에러가 발생했습니다."),
    FORBIDDEN(403, "A-002", "미인증 상태입니다."),
    UNAUTHORIZED(401, "A-003", "해당 리소스에 대한 접근 권한이 없습니다."),
    EMAIL_DUPLICATED(400, "A-004", "중복되는 이메일이 존재합니다."),
    EMAIL_NOT_FOUND(400, "A-005", "해당 이메일이 존재하지 않습니다."),
    PASSWORD_DIFFERENT(400, "A-006", "비밀번호가 일치하지 않습니다."),
    REFRESH_TOKEN_DIFFERENT(400, "A-007", "재발급 토큰 값이 다릅니다."),
    USER_NOT_FOUND(404, "A-008", "해당 유저를 찾을 수 없습니다."),
    REFRESH_TOKEN_NOT_FOUND(404, "A-009", "재발급 토큰이 만료되었거나 찾을 수 없습니다."),
    NOT_VALID(400, "A-010", "잘못된 요청입니다.")

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