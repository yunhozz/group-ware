package com.mailservice.common.enums;

public enum SecurityRating {

    GENERAL("일반"),
    CONFIDENTIAL("기밀")

    ;

    private final String desc;

    SecurityRating(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}