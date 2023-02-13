package com.notificationservice.application.exception;

import com.notificationservice.common.exception.ErrorCode;
import com.notificationservice.common.exception.NotificationException;

public class NotificationNotFoundException extends NotificationException {

    public NotificationNotFoundException() {
        super(ErrorCode.NOTIFICATION_NOT_FOUND);
    }
}