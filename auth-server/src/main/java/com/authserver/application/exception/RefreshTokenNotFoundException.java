package com.authserver.application.exception;

import com.authserver.common.enums.ErrorCode;
import com.authserver.common.exception.AuthException;

public class RefreshTokenNotFoundException extends AuthException {

    public RefreshTokenNotFoundException() {
        super(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
    }
}