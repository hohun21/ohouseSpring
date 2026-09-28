package com.ohouse.web.service.auth;

import com.ohouse.web.domain.member.MemberVO;
import com.ohouse.web.mapper.auth.MemberAuthMapper;
import com.ohouse.web.security.LegacyPasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LegacyPasswordUpgradeService {
    private final MemberAuthMapper mapper;
    private final PasswordEncoder encoder;

    @Transactional
    public void upgrade(MemberVO member, String rawPassword) {
        if (LegacyPasswordEncoder.isBcrypt(member.getPassword())) return;
        String hash = encoder.encode(rawPassword);
        if (mapper.upgradeLegacyPassword(member.getId(), member.getPassword(), hash) != 1)
            throw new IllegalStateException("비밀번호 전환 중 계정 정보가 변경되었습니다. 다시 로그인해주세요.");
        member.setPassword(hash);
    }
}
