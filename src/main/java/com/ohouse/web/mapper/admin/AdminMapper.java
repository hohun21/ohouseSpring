package com.ohouse.web.mapper.admin;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ohouse.web.domain.member.MemberDTO;

public interface AdminMapper {
    
    List<MemberDTO> getAllMembers();
    
    int getTotalMemberCount();
    
    List<MemberDTO> getMemberListWithPaging(@Param("startRow") int startRow, @Param("endRow") int endRow);
    
    int updateMemberStatus(@Param("memberId") int memberId, @Param("status") int status);
}