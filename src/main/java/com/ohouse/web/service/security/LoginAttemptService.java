package com.ohouse.web.service.security;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ohouse.web.mapper.security.LoginAttemptMapper;

@Service
public class LoginAttemptService {

    private static final int MAX_FAILURES = 10;

    private final LoginAttemptMapper loginAttemptMapper;

    public LoginAttemptService(LoginAttemptMapper loginAttemptMapper) {
        this.loginAttemptMapper = loginAttemptMapper;
    }

    public boolean isLocked(String loginId) {
        return loginAttemptMapper.existsActiveLock(loginId) > 0;
    }
    
    public int getRecentFailureCount(String loginId) {
    	return loginAttemptMapper.countRecentFailures(loginId);
    }

    @Transactional
    public void recordFailure(String loginId, String ipAddress) {
        loginAttemptMapper.insertFailure(loginId, ipAddress);

        int failures = loginAttemptMapper.countRecentFailures(loginId);

        if (failures == MAX_FAILURES) {
            loginAttemptMapper.setLockedUntilLatestFailure(loginId);
        }
    }
    
    @Transactional
    public void reset(String loginId) {
        loginAttemptMapper.deleteFailuresByLoginId(loginId);
    }
}