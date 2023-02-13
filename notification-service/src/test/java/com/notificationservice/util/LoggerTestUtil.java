package com.notificationservice.util;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.slf4j.LoggerFactory;

public class LoggerTestUtil {

    public static ListAppender<ILoggingEvent> getListAppenderForClass(Class clazz) {
        Logger logger = (Logger) LoggerFactory.getLogger(clazz);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();

        listAppender.start(); // 로그 기록 시작
        logger.addAppender(listAppender); // logger 에 ListAppender 를 추가하여 발생하는 로그를 List 에 저장
        return listAppender;
    }
}