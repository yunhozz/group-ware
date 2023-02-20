package com.teamservice.application.exception;

import com.teamservice.common.exception.ErrorCode;
import com.teamservice.common.exception.TeamException;

public class NotLeaderException extends TeamException {

    public NotLeaderException() {
        super(ErrorCode.NOT_LEADER);
    }
}