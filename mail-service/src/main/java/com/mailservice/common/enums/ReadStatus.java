package com.mailservice.common.enums;

import lombok.Getter;

@Getter
public enum ReadStatus {

    READ("읽음"),
    NOT_READ("읽지 않음")

    ;

    private final String desc;

    ReadStatus(String desc) {
        this.desc = desc;
    }
}