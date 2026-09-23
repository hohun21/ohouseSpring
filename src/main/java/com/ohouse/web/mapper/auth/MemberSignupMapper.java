package com.ohouse.web.mapper.auth;

import java.sql.SQLException;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.ohouse.web.domain.member.MemberVO;

@Repository
public interface MemberSignupMapper {

	public MemberVO getMember(@Param("id") String id) throws ClassNotFoundException, SQLException;
	
	public int insert(MemberVO memberVO) throws ClassNotFoundException, SQLException;

	public int insertAuthority(@Param("id") String id) throws ClassNotFoundException, SQLException;

}
