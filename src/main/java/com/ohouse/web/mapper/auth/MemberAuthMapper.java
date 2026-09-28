package com.ohouse.web.mapper.auth;

import org.apache.ibatis.annotations.Param;
import com.ohouse.web.domain.member.MemberVO;

public interface MemberAuthMapper {
    MemberVO getMember(@Param("id") String id);
    int insert(MemberVO memberVO);
    int insertAuthority(@Param("id") String id);
    MemberVO read(@Param("id") String id);
    Integer statusCheck(@Param("id") String id);
    int countByName(@Param("name") String name);
    Integer getMemberId(@Param("id") String id);
    int insertCart(@Param("memberId") Integer memberId);
    int updatePassword(@Param("id") String id, @Param("password") String password);
    int upgradeLegacyPassword(@Param("id") String id, @Param("oldPassword") String oldPassword, @Param("password") String password);
}
