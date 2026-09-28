package com.ohouse.web.service.auth;

import org.springframework.stereotype.Service;
import com.ohouse.web.mapper.auth.MemberAuthMapper;
import lombok.RequiredArgsConstructor;

/** 로그인 화면의 회원 상태와 가입 정보 확인. 비밀번호 검증은 Spring Security가 담당한다. */
@Service
@RequiredArgsConstructor
public class MemberLoginService {
    private final MemberAuthMapper memberAuthMapper;
    public Integer statusCheck(String id) { return memberAuthMapper.statusCheck(id); }
    public boolean idExists(String id) { return memberAuthMapper.getMember(id) != null; }
    public boolean nameExists(String name) { return memberAuthMapper.countByName(name) > 0; }
}
