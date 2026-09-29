package com.ohouse.web.mapper.order;

import com.ohouse.web.domain.member.CouponDTO;
import com.ohouse.web.domain.member.MyOrderDTO;
import com.ohouse.web.domain.member.MyOrderDetailDTO;
import com.ohouse.web.domain.order.OrderDetailRequestDTO;
import com.ohouse.web.domain.order.OrderRequestDTO;
import com.ohouse.web.domain.order.OrderStatusCountDTO;
import org.apache.ibatis.annotations.Param;

import java.sql.SQLException;
import java.util.List;

public interface OrderMapper {
    int selectOrderId();

    int insertOrder(
            @Param("order_id") int order_id,
            @Param("dto") OrderRequestDTO dto,
            @Param("member_id") int memberId
    );

    long findBrandIdByProductOptionId(
            long productOptionId
    );

    int insertOrderDetail(
            @Param("order_id") int orderId,
            @Param("dto") OrderDetailRequestDTO dto
    );

    int updateStock(
            OrderDetailRequestDTO dto
    );
    List<MyOrderDTO> viewMyOrder(int member_id);

    List<MyOrderDetailDTO> viewMyOrderDetail(int order_id);

    OrderStatusCountDTO getOrderStatusCount(@Param("member_id") int member_id);

    void payConfirm(int orders_detail_id) throws SQLException;

    void payCancel(int orders_detail_id) throws SQLException;

    void payReturn(int orders_detail_id) throws SQLException;

    // 쿠폰 관련
    List<CouponDTO> myCouponlist(int member_id) throws SQLException;

    int getCouponCount(int member_id) throws SQLException;
}
