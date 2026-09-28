package com.ohouse.web.service.member;

import com.ohouse.web.domain.member.MyOrderDTO;

import java.util.List;

public interface MemberService {

    List<MyOrderDTO> selectorder(int member_id);
}
