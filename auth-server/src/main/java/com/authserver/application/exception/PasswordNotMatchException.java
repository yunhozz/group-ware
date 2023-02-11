package com.authserver.application.exception;

import com.authserver.common.enums.ErrorCode;
import com.authserver.common.exception.AuthException;

public class PasswordNotMatchException extends AuthException {

    public PasswordNotMatchException() {
        super(ErrorCode.PASSWORD_DIFFERENT);
    }
}