package com.ohouse.web.exception;

import org.springframework.security.core.AuthenticationException;

public class MemberStatusAuthenticationException extends AuthenticationException {

    private final int status;

    public MemberStatusAuthenticationException(int status) {
        super(" 로그인할 수 없는 계정입니다. ");
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}