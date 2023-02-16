package com.postservice.application.exception;

import com.postservice.common.exception.ErrorCode;
import com.postservice.common.exception.PostException;

public class FileUploadFailException extends PostException {

    public FileUploadFailException() {
        super(ErrorCode.FILE_UPLOAD_FAIL);
    }
}