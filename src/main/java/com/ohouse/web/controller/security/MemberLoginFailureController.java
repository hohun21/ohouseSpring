package com.ohouse.web.controller.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.ohouse.web.exception.LoginLockedException;
import com.ohouse.web.exception.MemberStatusAuthenticationException;
import com.ohouse.web.service.security.LoginAttemptService;

@Component("memberLoginFailureController")
public class MemberLoginFailureController
implements AuthenticationFailureHandler {

	private final LoginAttemptService loginAttemptService;

	public MemberLoginFailureController(
			LoginAttemptService loginAttemptService) {
		this.loginAttemptService = loginAttemptService;
	}

	@Override
	public void onAuthenticationFailure(
			HttpServletRequest request,
			HttpServletResponse response,
			AuthenticationException exception)
					throws IOException, ServletException {

		String loginPage = request.getContextPath() + "/auth/login.htm";

		if (exception instanceof LoginLockedException) {
			response.sendRedirect(loginPage + "?error=locked");
			return;
		}

		if (exception instanceof MemberStatusAuthenticationException) {
			response.sendRedirect(loginPage + "?error=status");
			return;
		}

		String id = request.getParameter("username");
		int failureCount = 0;

		if (id != null && !id.trim().isEmpty()) {
			failureCount =
					loginAttemptService.getRecentFailureCount(id.trim());
		}

		response.sendRedirect(
				loginPage + "?error=invalid&count=" + failureCount);
	}
}