package com.postservice.application.exception;

import com.postservice.common.exception.ErrorCode;
import com.postservice.common.exception.PostException;

public class WriterDifferentException extends PostException {

    public WriterDifferentException() {
        super(ErrorCode.WRITER_DIFFERENT);
    }
}