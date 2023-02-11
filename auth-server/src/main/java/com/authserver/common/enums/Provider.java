package com.authserver.common.enums;

import lombok.Getter;

@Getter
public enum Provider {

    LOCAL("로컬 계정"),
    GOOGLE("구글 계정"),
    KAKAO("카카오 계정"),
    NAVER("네이버 계정")

    ;

    private final String desc;

    Provider(String desc) {
        this.desc = desc;
    }
}