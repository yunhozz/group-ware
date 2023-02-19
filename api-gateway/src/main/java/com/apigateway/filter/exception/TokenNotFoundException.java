package com.apigateway.filter.exception;

public class TokenNotFoundException extends RuntimeException {

    public TokenNotFoundException() {
        super("헤더에 jwt 토큰이 존재하지 않습니다.");
    }
}