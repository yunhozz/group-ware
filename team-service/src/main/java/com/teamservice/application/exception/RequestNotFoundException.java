package com.teamservice.application.exception;

import com.teamservice.common.exception.ErrorCode;
import com.teamservice.common.exception.TeamException;

public class RequestNotFoundException extends TeamException {

    public RequestNotFoundException() {
        super(ErrorCode.REQUEST_NOT_FOUND);
    }
}