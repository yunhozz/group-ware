package com.postservice.common.enums;

import lombok.Getter;

@Getter
public enum PostType {

    MUST_READ("필독"),
    NOTICE("공지 사항"),
    REPORT("업무 보고")

    ;

    private final String desc;

    PostType(String desc) {
        this.desc = desc;
    }
}