package com.ohouse.web.service.order;

import com.ohouse.web.domain.order.OrderDetailRequestDTO;
import com.ohouse.web.domain.order.OrderRequestDTO;
import com.ohouse.web.mapper.cart.CartMapper;
import com.ohouse.web.mapper.order.OrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final CartMapper cartMapper;
    private final OrderMapper orderMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertOrder(int member_id, OrderRequestDTO dto, List<Integer> cart_items_ids) throws Exception {
        Integer cartId = null;


        // ========================================
        // 1. 장바구니 주문인지 확인
        // ========================================

        if (cart_items_ids != null && !cart_items_ids.isEmpty()) {

            cartId = cartMapper.findCartID(member_id);
        }


        // ========================================
        // 2. 주문 INSERT
        // ========================================
        int order_id = orderMapper.selectOrderId();
        orderMapper.insertOrder(order_id, dto, member_id);


        // ========================================
        // 3. 주문 상세 + 재고 차감
        // ========================================

        for (OrderDetailRequestDTO odrDTO : dto.getOrderDetails()) {


            // ------------------------------------
            // 상품 옵션으로 브랜드 ID 조회
            // ------------------------------------

            long brandId = orderMapper.findBrandIdByProductOptionId(odrDTO.getProductOptionId());

            odrDTO.setBrandId(brandId);


            // ------------------------------------
            // 주문 상세 INSERT
            // ------------------------------------

            orderMapper.insertOrderDetail(order_id, odrDTO);


            // ------------------------------------
            // 재고 차감
            // ------------------------------------

            int result = orderMapper.updateStock(odrDTO);


            // 재고 부족
            if (result == 0) {
                throw new SQLException("재고가 부족합니다.");
            }
        }


        // ========================================
        // 4. 주문한 장바구니 상품 삭제
        // ========================================

        if (cartId != null) {

            cartMapper.deleteCartItems(cartId, cart_items_ids);


            // ====================================
            // 5. 장바구니 총액 갱신
            // ====================================

            cartMapper.updateTotalPrice(cartId);
        }

        // @Transactional이 COMMIT 처리
        return order_id;
    }
}
