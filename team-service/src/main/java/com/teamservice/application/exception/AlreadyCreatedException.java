package com.teamservice.application.exception;

import com.teamservice.common.exception.ErrorCode;
import com.teamservice.common.exception.TeamException;

public class AlreadyCreatedException extends TeamException {

    public AlreadyCreatedException() {
        super(ErrorCode.ALREADY_CREATED);
    }
}