package com.postservice.common.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    INTER_SERVER_ERROR(500, "P-001", "서버 에러가 발생했습니다."),
    NOT_AUTHORIZED(401, "P-002", "해당 리소스에 대한 접근 권한이 없습니다."),
    NOT_VALID(400, "P-003", "잘못된 요청입니다."),
    POST_NOT_FOUND(404, "P-004", "해당 게시물을 찾을 수 없습니다."),
    WRITER_DIFFERENT(400, "P-005", "작성자만 수정 권한이 있습니다."),
    FILE_UPLOAD_FAIL(400, "P-006", "파일 업로드에 실패하였습니다.")

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