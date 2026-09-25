package com.ohouse.web.domain.security;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import com.ohouse.web.domain.member.MemberVO;

import lombok.Getter;
import lombok.extern.log4j.Log4j;

@Getter
@Log4j
public class CustomerUser extends User {

	private MemberVO member_vo;
	
	public CustomerUser(String username
					, String password
					, boolean enabled
					, boolean accountNonExpired
					, boolean credentialsNonExpired
					, boolean accountNonLocked
					, Collection<? extends GrantedAuthority> authorities) {
		super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
		
		log.info("❤️ CustomUserDetailsService.loadUserByUsername...");
	}
	
	public CustomerUser(MemberVO memberVO) {
	    super(
	        memberVO.getId(),
	        memberVO.getPassword(),
	        memberVO.getStatus() == 1, // enabled
	        true,                      // accountNonExpired
	        true,                      // credentialsNonExpired
	        true,                      // accountNonLocked
	        memberVO.getAuthList().stream()
	            .map(auth -> new SimpleGrantedAuthority(auth.getAuthority()))
	            .collect(Collectors.toList())
	    );

	    this.member_vo = memberVO;

	    log.info("❤️ CustomerUser : " + member_vo);
	}
	
	
}
