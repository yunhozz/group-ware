package com.mailservice.application.exception;

import com.mailservice.common.exception.EmailException;
import com.mailservice.common.exception.ErrorCode;

public class FileUploadFailException extends EmailException {

    public FileUploadFailException() {
        super(ErrorCode.FILE_UPLOAD_FAIL);
    }
}