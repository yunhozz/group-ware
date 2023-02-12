package com.userservice.interfaces.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Slf4j
@Component
@RequiredArgsConstructor
public class HeaderTokenResolver implements HandlerMethodArgumentResolver {

    private final TokenUtils tokenUtils;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean hasHeaderTokenAnnotation = parameter.hasParameterAnnotation(HeaderToken.class);
        boolean isStringType = String.class.isAssignableFrom(parameter.getParameterType());
        return hasHeaderTokenAnnotation && isStringType;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        String auth = (String) tokenUtils.parseToken(token).get("auth");

        HeaderToken headerToken = parameter.getParameterAnnotation(HeaderToken.class);
        Role[] roles = headerToken.role();

        if (!isAuthorized(roles, auth)) {
            throw new IllegalStateException("권한이 없습니다.");
        }

        return token;
    }

    private boolean isAuthorized(Role[] roles, String auth) {
        for (Role role : roles) {
            if (role.getAuth().equals(auth)) {
                return true;
            }
        }

        return false;
    }
}