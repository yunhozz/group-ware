package com.authserver.application.exception;

import com.authserver.common.enums.ErrorCode;
import com.authserver.common.exception.AuthException;

public class UserNotFoundException extends AuthException {

    public UserNotFoundException() {
        super(ErrorCode.USER_NOT_FOUND);
    }
}