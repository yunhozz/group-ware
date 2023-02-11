package com.authserver.application.exception;

import com.authserver.common.enums.ErrorCode;
import com.authserver.common.exception.AuthException;

public class EmailDuplicateException extends AuthException {

    public EmailDuplicateException() {
        super(ErrorCode.EMAIL_DUPLICATED);
    }
}