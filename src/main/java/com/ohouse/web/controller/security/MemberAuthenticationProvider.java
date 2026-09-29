package com.ohouse.web.controller.security;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

import com.ohouse.web.domain.member.MemberVO;
import com.ohouse.web.domain.security.CustomerUser;
import com.ohouse.web.exception.LoginLockedException;
import com.ohouse.web.exception.MemberStatusAuthenticationException;
import com.ohouse.web.service.security.CustomUserDetailsService;
import com.ohouse.web.service.security.LoginAttemptService;

@Component("memberAuthenticationProvider")
public class MemberAuthenticationProvider implements AuthenticationProvider {

    private final CustomUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final LoginAttemptService loginAttemptService;

    public MemberAuthenticationProvider(
            CustomUserDetailsService userDetailsService,
            @Qualifier("bCryptPasswordEncoder") PasswordEncoder passwordEncoder,
            LoginAttemptService loginAttemptService) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.loginAttemptService = loginAttemptService;
    }

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {
    	System.out.println("=== MemberAuthenticationProvider 실행 ===");
    	
        String id = authentication.getName();
        String rawPassword = authentication.getCredentials() == null
                ? ""
                : authentication.getCredentials().toString();

        // 잠금 중이면 실패 로그를 추가하지 않고 차단
        if (loginAttemptService.isLocked(id)) {
            throw new LoginLockedException();
        }

        final CustomerUser user;
        try {
            user = (CustomerUser) userDetailsService.loadUserByUsername(id);
        } catch (UsernameNotFoundException e) {
            loginAttemptService.recordFailure(id, getClientIp(authentication));
            throw new BadCredentialsException("아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
        	System.out.println("=== 비밀번호 불일치 ===");
        	loginAttemptService.recordFailure(id, getClientIp(authentication));
            throw new BadCredentialsException("아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        MemberVO member = user.getMember_vo();

        // 계정 상태 확인
        if (member.getStatus() != 1) {
            throw new MemberStatusAuthenticationException(member.getStatus());
        }

        // 비밀번호가 맞은 경우 현재까지의 실패 기록 초기화
        loginAttemptService.reset(id);

        return new UsernamePasswordAuthenticationToken(
                user,
                null,
                user.getAuthorities()
        );
    }

    private String getClientIp(Authentication authentication) {
        Object details = authentication.getDetails();

        if (details instanceof WebAuthenticationDetails) {
            return ((WebAuthenticationDetails) details).getRemoteAddress();
        }

        return null;
    }
    
    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class
                .isAssignableFrom(authentication);
    }
}