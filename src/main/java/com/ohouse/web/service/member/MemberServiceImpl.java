package com.ohouse.web.service.member;

import com.ohouse.web.domain.member.CouponDTO;
import com.ohouse.web.domain.member.MyOrderDTO;
import com.ohouse.web.domain.member.MyOrderDetailDTO;
import com.ohouse.web.mapper.order.OrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final OrderMapper orderMapper;
    @Override
    public List<MyOrderDTO> selectorder(int member_id) {
        List<MyOrderDTO> list = this.orderMapper.viewMyOrder(member_id);
        for (MyOrderDTO myOrderDTO : list) {
            System.out.println("orders_id = " + myOrderDTO.getOrders_id());
            List<MyOrderDetailDTO> myOrderDetail = this.orderMapper.viewMyOrderDetail(myOrderDTO.getOrders_id());
            myOrderDTO.setOrderDetails(myOrderDetail);
        }
        return list;
    }

    @Override
    public List<CouponDTO> selectCoupon(int member_id) throws SQLException {
        List<CouponDTO> clist = this.orderMapper.myCouponlist(member_id);
        return clist;
    }
}
