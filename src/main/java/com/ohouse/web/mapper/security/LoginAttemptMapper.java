package com.ohouse.web.mapper.security;

import org.apache.ibatis.annotations.Param;

public interface LoginAttemptMapper {

	int existsActiveLock(@Param("loginId") String loginId);

	int insertFailure(@Param("loginId") String loginId, @Param("ipAddress") String ipAddress);

	int countRecentFailures(@Param("loginId") String loginId);

	int setLockedUntilLatestFailure(@Param("loginId") String loginId);

	int deleteFailuresByLoginId(@Param("loginId") String loginId);

	int deleteExpiredFailures();
}