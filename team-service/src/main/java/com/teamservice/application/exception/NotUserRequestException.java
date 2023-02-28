package com.teamservice.application.exception;

import com.teamservice.common.exception.ErrorCode;
import com.teamservice.common.exception.TeamException;

public class NotUserRequestException extends TeamException {

    public NotUserRequestException() {
        super(ErrorCode.NOT_USER_REQUEST);
    }
}