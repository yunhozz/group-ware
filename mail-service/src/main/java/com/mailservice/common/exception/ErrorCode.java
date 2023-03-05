package com.mailservice.common.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    INTER_SERVER_ERROR(500, "E-001", "서버 에러가 발생했습니다."),
    NOT_VALID(400, "E-002", "잘못된 요청입니다."),
    EMAIL_NOT_FOUND(404, "E-003", "해당 이메일을 찾을 수 없습니다."),
    FILE_NOT_FOUND(404, "E-004", "해당 파일을 찾을 수 없습니다."),
    FILE_DOWNLOAD_FAIL(400, "E-005", "파일 다운로드에 실패하였습니다."),
    FILE_UPLOAD_FAIL(400, "E-006", "파일 업로드에 실패하였습니다."),
    EMAIL_SEND_FAIL(400, "E-007", "이메일 전송에 실패하였습니다.")

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