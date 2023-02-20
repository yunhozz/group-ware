package com.teamservice.application.exception;

import com.teamservice.common.exception.ErrorCode;
import com.teamservice.common.exception.TeamException;

public class TeamNotFoundException extends TeamException {

    public TeamNotFoundException() {
        super(ErrorCode.TEAM_NOT_FOUND);
    }
}