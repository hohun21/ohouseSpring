package com.ohouse.web.service.order;

import com.ohouse.web.domain.order.OrderRequestDTO;

import java.util.List;

public interface OrderService {

    int insertOrder(int member_id, OrderRequestDTO dto, List<Integer> cart_items_ids) throws Exception;

}
