package com.mailservice.common.enums;

import lombok.Getter;

@Getter
public enum MailType {

    BASIC("일반 메일"),
    IMPORTANT("중요 메일")

    ;

    private final String desc;

    MailType(String desc) {
        this.desc = desc;
    }
}