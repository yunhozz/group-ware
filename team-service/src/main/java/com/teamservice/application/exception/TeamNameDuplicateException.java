package com.teamservice.application.exception;

import com.teamservice.common.exception.ErrorCode;
import com.teamservice.common.exception.TeamException;

public class TeamNameDuplicateException extends TeamException {

    public TeamNameDuplicateException() {
        super(ErrorCode.TEAM_NAME_DUPLICATE);
    }
}