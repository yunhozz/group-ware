package com.authserver.application.exception;

import com.authserver.common.enums.ErrorCode;
import com.authserver.common.exception.AuthException;

public class RefreshTokenDifferentException extends AuthException {

    public RefreshTokenDifferentException() {
        super(ErrorCode.REFRESH_TOKEN_DIFFERENT);
    }
}