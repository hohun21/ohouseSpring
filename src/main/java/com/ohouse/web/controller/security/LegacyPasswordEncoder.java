package com.ohouse.web.controller.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/** Allows one successful login with a password stored by the old JSP application. */
public class LegacyPasswordEncoder implements PasswordEncoder {
    private final BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
    @Override public String encode(CharSequence rawPassword) { return bcrypt.encode(rawPassword); }
    @Override public boolean matches(CharSequence rawPassword, String stored) {
        if (rawPassword == null || stored == null) return false;
        if (isBcrypt(stored)) return bcrypt.matches(rawPassword, stored);
        return MessageDigest.isEqual(rawPassword.toString().getBytes(StandardCharsets.UTF_8),
                stored.getBytes(StandardCharsets.UTF_8));
    }
    public static boolean isBcrypt(String value) { return value != null && value.matches("\\$2[aby]\\$\\d{2}\\$[./A-Za-z0-9]{53}"); }
}
