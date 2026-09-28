package com.ohouse.web.service.auth;

import com.ohouse.web.domain.member.MemberVO;
import com.ohouse.web.mapper.auth.MemberAuthMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PasswordChangeService {
    private final MemberAuthMapper mapper;
    private final PasswordEncoder encoder;

    public boolean check(String id, String password) {
        MemberVO member = mapper.getMember(id);
        return member != null && member.getStatus() == 1 && password != null && encoder.matches(password, member.getPassword());
    }
    @Transactional
    public void change(String id, String current, String next) {
        if (!check(id, current)) throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        if (mapper.updatePassword(id, encoder.encode(next)) != 1) throw new IllegalStateException("비밀번호 변경에 실패했습니다.");
        mapper.deletePersistentTokens(id);
    }
}
