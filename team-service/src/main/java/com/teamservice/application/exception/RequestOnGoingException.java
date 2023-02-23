package com.teamservice.application.exception;

import com.teamservice.common.exception.ErrorCode;
import com.teamservice.common.exception.TeamException;

public class RequestOnGoingException extends TeamException {

    public RequestOnGoingException() {
        super(ErrorCode.REQUEST_ON_GOING);
    }
}