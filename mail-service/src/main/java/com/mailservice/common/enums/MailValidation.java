package com.mailservice.common.enums;

import lombok.Getter;

@Getter
public enum MailValidation {

    ONE_DAY("하루"),
    ONE_WEEK("일주일"),
    ONE_MONTH("한달")

    ;

    private final String desc;

    MailValidation(String desc) {
        this.desc = desc;
    }
}