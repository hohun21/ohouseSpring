package com.ohouse.web.service.member;

import com.ohouse.web.domain.member.CouponDTO;
import com.ohouse.web.domain.member.MyOrderDTO;

import java.sql.SQLException;
import java.util.List;

public interface MemberService {

    List<MyOrderDTO> selectorder(int member_id);

    List<CouponDTO> selectCoupon(int member_id) throws SQLException;
}
