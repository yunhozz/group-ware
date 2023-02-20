package com.teamservice.application.exception;

import com.teamservice.common.exception.ErrorCode;
import com.teamservice.common.exception.TeamException;

public class NotBelongToTeamException extends TeamException {

    public NotBelongToTeamException() {
        super(ErrorCode.NOT_BELONG_TO_TEAM);
    }
}