package com.userservice.common.enums;

import lombok.Getter;

@Getter
public enum Role {

    ADMIN("ROLE_ADMIN"),
    USER("ROLE_USER"),
    GUEST("ROLE_GUEST")

    ;

    private final String auth;

    Role(String auth) {
        this.auth = auth;
    }
}