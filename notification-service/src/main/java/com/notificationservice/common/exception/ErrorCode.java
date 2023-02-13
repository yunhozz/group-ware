package com.notificationservice.common.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    INTER_SERVER_ERROR(500, "N-001", "서버 에러가 발생했습니다."),
    NOTIFICATION_NOT_FOUND(404, "N-002", "해당 알림을 찾을 수 없습니다."),
    DATA_SEND_FAIL(400, "N-003", "데이터 전송에 실패하였습니다."),
    NOT_AUTHORIZED(401, "N-004", "해당 리소스에 대한 접근 권한이 없습니다."),
    NOT_VALID(400, "N-005", "잘못된 요청입니다.")

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