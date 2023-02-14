package com.postservice.common.annotation;

import com.postservice.common.enums.Role;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface HeaderToken {

    Role[] role() default {Role.GUEST, Role.USER, Role.ADMIN};
}