package com.authserver.application.exception;

import com.authserver.common.enums.ErrorCode;
import com.authserver.common.exception.AuthException;

public class EmailNotFoundException extends AuthException {

    public EmailNotFoundException() {
        super(ErrorCode.EMAIL_NOT_FOUND);
    }
}