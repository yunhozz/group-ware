package com.mailservice.application.exception;

import com.mailservice.common.exception.EmailException;
import com.mailservice.common.exception.ErrorCode;

public class EmailSendFailException extends EmailException {

    public EmailSendFailException() {
        super(ErrorCode.EMAIL_SEND_FAIL);
    }
}