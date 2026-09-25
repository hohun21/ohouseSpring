package com.ohouse.web.mapper.auth;

import java.sql.SQLException;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.ohouse.web.domain.member.MemberVO;

@Repository
public interface MemberAuthMapper {

	// 회원가입
	public MemberVO getMember(@Param("id") String id);
	public int insert(MemberVO memberVO);
	public int insertAuthority(@Param("id") String id);

	// 로그인
	public MemberVO read(@Param("id") String id);
	public Integer statusCheck(@Param("id") String id);
	
}
