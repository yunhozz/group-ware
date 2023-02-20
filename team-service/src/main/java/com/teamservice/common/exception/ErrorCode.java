package com.teamservice.common.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    INTER_SERVER_ERROR(500, "T-001", "서버 에러가 발생했습니다."),
    NOT_AUTHORIZED(401, "T-002", "해당 리소스에 대한 접근 권한이 없습니다."),
    NOT_VALID(400, "T-003", "잘못된 요청입니다."),
    TEAM_NOT_FOUND(404, "T-004", "해당 팀이 존재하지 않습니다."),
    ALREADY_CREATED(400, "T-005", "이미 생성한 팀이 존재합니다."),
    TEAM_NAME_DUPLICATE(400, "T-006", "중복되는 팀명이 존재합니다."),
    ALREADY_JOINED(400, "T-007", "이미 가입한 팀입니다."),
    NOT_LEADER(400, "T-008", "팀 리더만 수정/삭제가 가능합니다."),
    UPDATE_NOT_ALLOWED(400, "T-009", "팀 정보 수정은 24시간에 한번씩 가능합니다."),
    DELETE_NOT_ALLOWED(400, "T-010", "팀 삭제는 생성시간 3일 이후 가능합니다."),
    NOT_BELONG_TO_TEAM(400, "T-011", "해당 팀에 소속되어 있지 않습니다.")

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