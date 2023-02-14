package com.postservice.interfaces.exception;

import com.postservice.common.exception.ErrorCode;
import com.postservice.common.exception.PostException;

public class NotAuthorizedException extends PostException {

    public NotAuthorizedException() {
        super(ErrorCode.NOT_AUTHORIZED);
    }
}