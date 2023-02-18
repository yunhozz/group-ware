package com.postservice.application.exception;

import com.postservice.common.exception.ErrorCode;
import com.postservice.common.exception.PostException;

public class CommentNotFoundException extends PostException {

    public CommentNotFoundException() {
        super(ErrorCode.COMMENT_NOT_FOUND);
    }
}