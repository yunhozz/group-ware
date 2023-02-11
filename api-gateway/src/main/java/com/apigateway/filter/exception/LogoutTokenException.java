package com.apigateway.filter.exception;

public class LogoutTokenException extends RuntimeException {

    public LogoutTokenException() {
        super("로그아웃 토큰의 요청입니다. 다시 발급해주세요.");
    }
}