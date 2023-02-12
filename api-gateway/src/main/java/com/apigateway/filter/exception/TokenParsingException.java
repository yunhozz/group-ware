package com.apigateway.filter.exception;

public class TokenParsingException extends RuntimeException {

    public TokenParsingException(String message) {
        super(message);
    }
}