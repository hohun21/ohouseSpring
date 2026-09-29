package com.ohouse.web.exception;

import org.springframework.security.core.AuthenticationException;

public class LoginLockedException extends AuthenticationException {

    public LoginLockedException() {
        super("로그인 시도가 제한되었습니다. 잠시 후 다시 시도해주세요.");
    }
}