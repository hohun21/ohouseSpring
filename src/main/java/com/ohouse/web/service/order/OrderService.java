package com.ohouse.web.service.order;

import com.ohouse.web.domain.order.OrderRequestDTO;

import javax.naming.NamingException;
import java.sql.SQLException;
import java.util.List;

public interface OrderService {

    void insertOrder(int member_id, OrderRequestDTO dto, List<Integer> cart_items_ids) throws Exception;

}
