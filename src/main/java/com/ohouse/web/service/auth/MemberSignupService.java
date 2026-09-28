package com.ohouse.web.service.auth;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ohouse.web.domain.member.MemberVO;
import com.ohouse.web.mapper.auth.MemberAuthMapper;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberSignupService {
    private final MemberAuthMapper memberAuthMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void register(MemberVO memberVO) {
        if (memberAuthMapper.getMember(memberVO.getId()) != null)
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        if (memberAuthMapper.countByName(memberVO.getName()) > 0)
            throw new IllegalArgumentException("이미 사용 중인 이름입니다.");
        memberVO.setPassword(passwordEncoder.encode(memberVO.getPassword()));
        if (memberAuthMapper.insert(memberVO) != 1)
            throw new IllegalStateException("회원가입에 실패했습니다.");
        if (memberAuthMapper.insertAuthority(memberVO.getId()) != 1)
            throw new IllegalStateException("회원 권한 등록에 실패했습니다.");
        Integer memberId = memberAuthMapper.getMemberId(memberVO.getId());
        if (memberId == null || memberAuthMapper.insertCart(memberId) != 1)
            throw new IllegalStateException("장바구니 생성에 실패했습니다.");
    }
}
