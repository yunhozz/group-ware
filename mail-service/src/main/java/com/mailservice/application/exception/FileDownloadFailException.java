package com.mailservice.application.exception;

import com.mailservice.common.exception.EmailException;
import com.mailservice.common.exception.ErrorCode;

public class FileDownloadFailException extends EmailException {

    public FileDownloadFailException() {
        super(ErrorCode.FILE_DOWNLOAD_FAIL);
    }
}