package com.ohouse.web.mapper.admin;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ohouse.web.domain.admin.CouponDTO;

public interface CouponMapper {
    
    List<CouponDTO> selectAllCoupons();
    
    void insertCoupon(CouponDTO coupon);
    
    void updateCouponStatus(@Param("couponId") int couponId, @Param("status") int status);
    
    List<Integer> selectAllActiveMemberIds();
    
    void insertMemberCouponBatch(@Param("couponId") int couponId, @Param("memberIds") List<Integer> memberIds);
}