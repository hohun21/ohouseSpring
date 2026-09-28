package com.ohouse.web.service.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.ohouse.web.domain.member.MemberVO;
import com.ohouse.web.domain.security.CustomerUser;
import com.ohouse.web.mapper.auth.MemberAuthMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;

@Log4j
@Component
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

	private final MemberAuthMapper memberAuthMapper;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		log.info("❤️ CustomUserDetailsService.loadUserByUsername...");
		log.info("ℹ️ username : " + username );
		
		MemberVO memberVO = memberAuthMapper.read(username);
		
		if (memberVO == null) {
	        throw new UsernameNotFoundException("존재하지 않는 회원입니다: " + username);
	    }

	    return new CustomerUser(memberVO);
	}
}
