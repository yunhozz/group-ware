package com.teamservice.application.exception;

import com.teamservice.common.exception.ErrorCode;
import com.teamservice.common.exception.TeamException;

public class UpdateNotAllowedException extends TeamException {

    public UpdateNotAllowedException() {
        super(ErrorCode.UPDATE_NOT_ALLOWED);
    }
}