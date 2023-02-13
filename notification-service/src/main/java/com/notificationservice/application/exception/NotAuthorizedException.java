package com.notificationservice.application.exception;

import com.notificationservice.common.exception.ErrorCode;
import com.notificationservice.common.exception.NotificationException;

public class NotAuthorizedException extends NotificationException {

    public NotAuthorizedException() {
        super(ErrorCode.NOT_AUTHORIZED);
    }
}