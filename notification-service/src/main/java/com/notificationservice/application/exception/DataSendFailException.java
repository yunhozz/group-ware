package com.notificationservice.application.exception;

import com.notificationservice.common.exception.ErrorCode;
import com.notificationservice.common.exception.NotificationException;

public class DataSendFailException extends NotificationException {

    public DataSendFailException() {
        super(ErrorCode.DATA_SEND_FAIL);
    }
}