package com.teamservice.application.exception;

import com.teamservice.common.exception.ErrorCode;
import com.teamservice.common.exception.TeamException;

public class DeleteNotAllowedException extends TeamException {

    public DeleteNotAllowedException() {
        super(ErrorCode.DELETE_NOT_ALLOWED);
    }
}