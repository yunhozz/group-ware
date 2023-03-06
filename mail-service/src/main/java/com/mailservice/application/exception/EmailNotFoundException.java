package com.mailservice.application.exception;

import com.mailservice.common.exception.EmailException;
import com.mailservice.common.exception.ErrorCode;

public class EmailNotFoundException extends EmailException {

    public EmailNotFoundException() {
        super(ErrorCode.EMAIL_NOT_FOUND);
    }
}