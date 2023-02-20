package com.teamservice.application.exception;

import com.teamservice.common.exception.ErrorCode;
import com.teamservice.common.exception.TeamException;

public class AlreadyJoinedException extends TeamException {

    public AlreadyJoinedException() {
        super(ErrorCode.ALREADY_JOINED);
    }
}