package com.ohouse.web.service.auth;

import java.sql.SQLException;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ohouse.web.domain.member.MemberVO;
import com.ohouse.web.mapper.auth.MemberSignupMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberSignupService {

    private final MemberSignupMapper memberMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void register(MemberVO memberVO) throws ClassNotFoundException, SQLException {
    	
    	if ( memberMapper.getMember(memberVO.getId()) != null ) {
    	    throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
    	}
    	
    	memberVO.setPassword( passwordEncoder.encode( memberVO.getPassword()) );

        int rowCount = memberMapper.insert(memberVO);

        if (rowCount != 1) {
            throw new IllegalStateException("회원가입 실패");
        }

        memberMapper.insertAuthority(memberVO.getId());
    }
}