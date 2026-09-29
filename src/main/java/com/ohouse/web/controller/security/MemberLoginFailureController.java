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

		// 잠금 상태는 새 실패 횟수로 기록하지 않음
		if (exception instanceof LoginLockedException) {
			response.sendRedirect(loginPage + "?error=locked");
			return;
		}

		// 비밀번호가 확인된 계정만 상태를 구분해 안내
		if (exception instanceof MemberStatusAuthenticationException) {
			int status =
					((MemberStatusAuthenticationException) exception).getStatus();

			if (status == 0) {
				response.sendRedirect(loginPage + "?error=withdrawn");
			} else if (status == -1) {
				response.sendRedirect(loginPage + "?error=suspended");
			} else {
				response.sendRedirect(loginPage + "?error=status");
			}
			return;
		}

		// 잘못된 아이디·비밀번호 실패 횟수 표시
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