package com.apigateway.filter.exception;

public class HaveNotAuthorityException extends RuntimeException {

    public HaveNotAuthorityException(String auth) {
        super("리소스 접근 권한이 없습니다. 관리자에게 문의해주세요. 현재 유저 권한 : " + auth);
    }
}