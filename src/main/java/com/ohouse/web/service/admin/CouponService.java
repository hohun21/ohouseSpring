package com.ohouse.web.service.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ohouse.web.domain.admin.CouponDTO;
import com.ohouse.web.mapper.admin.CouponMapper;

@Service
public class CouponService {
    
    @Autowired
    private CouponMapper couponMapper;

    public List<CouponDTO> getCouponList() throws Exception {
        return couponMapper.selectAllCoupons();
    }

    @Transactional
    public void addCoupon(CouponDTO coupon) throws Exception {
        couponMapper.insertCoupon(coupon);
    }

    @Transactional
    public void updateCouponStatus(int couponId, int status) throws Exception {
        couponMapper.updateCouponStatus(couponId, status);
    }

    @Transactional
    public void issueCouponToAll(int couponId) throws Exception {
        List<Integer> memberIds = couponMapper.selectAllActiveMemberIds();
        
        if (memberIds != null && !memberIds.isEmpty()) {
            couponMapper.insertMemberCouponBatch(couponId, memberIds);
        }
        
        couponMapper.updateCouponStatus(couponId, 1);
    }
}