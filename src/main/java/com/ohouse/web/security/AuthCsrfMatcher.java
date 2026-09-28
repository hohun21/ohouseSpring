package com.ohouse.web.security;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

/** Apply CSRF checks to this migration's state-changing routes only. */
@Component("authCsrfMatcher")
public class AuthCsrfMatcher implements RequestMatcher {
    private final Set<String> paths = new HashSet<>(Arrays.asList(
            "/auth/login", "/auth/signup.htm", "/seller/signup.htm", "/seller/login.htm",
            "/seller/sellerSignupStatus.htm", "/emailcheck.ajax", "/brandnamecheck.ajax",
            "/businessnumbercheck.ajax", "/mailordernumbercheck.ajax", "/checkCurrentPwd.ajax",
            "/changePwdPro.htm", "/logout.htm"));
    @Override
    public boolean matches(HttpServletRequest request) {
        return "POST".equalsIgnoreCase(request.getMethod()) && paths.contains(request.getServletPath());
    }
}
