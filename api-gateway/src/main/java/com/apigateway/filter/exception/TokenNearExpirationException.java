package com.apigateway.filter.exception;

public class TokenNearExpirationException extends RuntimeException {

    public TokenNearExpirationException() {
        super("토큰 유효시간이 얼마 남지 않았습니다. 재발급이 필요합니다.");
    }
}