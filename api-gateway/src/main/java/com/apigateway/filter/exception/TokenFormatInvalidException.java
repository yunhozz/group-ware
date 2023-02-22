package com.apigateway.filter.exception;

public class TokenFormatInvalidException extends RuntimeException {

    public TokenFormatInvalidException() {
        super("토큰 형식이 적합하지 않습니다.");
    }
}