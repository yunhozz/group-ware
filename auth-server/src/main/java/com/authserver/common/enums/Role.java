package com.authserver.common.enums;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {

    ADMIN("ROLE_ADMIN", "운영자", 1),
    USER("ROLE_USER", "일반 사용자", 2),
    GUEST("ROLE_GUEST", "게스트", 3)

    ;

    private final String auth;
    private final String desc;
    private final int order;

    Role(String auth, String desc, int order) {
        this.auth = auth;
        this.desc = desc;
        this.order = order;
    }

    @Override
    public String getAuthority() {
        return auth;
    }

    public String getDesc() {
        return desc;
    }

    public int getOrder() {
        return order;
    }
}