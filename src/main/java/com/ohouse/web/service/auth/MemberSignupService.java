package com.ohouse.web.service.auth;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ohouse.web.domain.member.MemberVO;
import com.ohouse.web.mapper.auth.MemberAuthMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberSignupService {

	private final MemberAuthMapper memberAuthMapper;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;

	@Transactional
	public void register(MemberVO memberVO) {
		if (memberAuthMapper.getMember(memberVO.getId()) != null) {
			throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
		}

		memberVO.setPassword(bCryptPasswordEncoder.encode(memberVO.getPassword()));

		int inserted = memberAuthMapper.insert(memberVO);
		if (inserted != 1) {
			throw new IllegalStateException("회원가입에 실패했습니다.");
		}

		int authorityInserted = memberAuthMapper.insertAuthority(memberVO.getId());
		if (authorityInserted != 1) {
			throw new IllegalStateException("회원 권한 등록에 실패했습니다.");
		}
	}
}