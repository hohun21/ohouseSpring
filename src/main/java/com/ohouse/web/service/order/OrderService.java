package com.ohouse.web.service.order;

import com.ohouse.web.domain.order.OrderRequestDTO;
import com.ohouse.web.domain.order.OrderStatusCountDTO;

import javax.naming.NamingException;
import java.sql.SQLException;
import java.util.List;

public interface OrderService {

    int insertOrder(int member_id, OrderRequestDTO dto, List<Integer> cart_items_ids) throws Exception;

    OrderStatusCountDTO getOrderStatusCount(int member_id) throws Exception;

    void payconfirm(int orders_detail_id) throws SQLException, NamingException;

    void paycancel(int orders_detail_id) throws SQLException, NamingException;

    void payreturn(int orders_detail_id) throws SQLException, NamingException;

    // 쿠폰
    int getCouponCount(int member_id) throws SQLException;

}
