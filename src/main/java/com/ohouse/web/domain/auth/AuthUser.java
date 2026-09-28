package com.ohouse.web.domain.auth;

import java.io.Serializable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AuthUser implements Serializable {
    private static final long serialVersionUID = 1L;
    private final Integer memberId;
    private final String id;
    private final String name;
    private final String role;
}
