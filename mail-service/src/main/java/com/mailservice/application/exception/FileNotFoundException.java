package com.mailservice.application.exception;

import com.mailservice.common.exception.EmailException;
import com.mailservice.common.exception.ErrorCode;

public class FileNotFoundException extends EmailException {

    public FileNotFoundException() {
        super(ErrorCode.FILE_NOT_FOUND);
    }
}