package com.postservice.application.exception;

import com.postservice.common.exception.ErrorCode;
import com.postservice.common.exception.PostException;

public class PostNotFoundException extends PostException {

    public PostNotFoundException() {
        super(ErrorCode.POST_NOT_FOUND);
    }
}