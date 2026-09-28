package com.ohouse.web.service.cart;

import com.ohouse.web.domain.cart.CartItemDTO;
import com.ohouse.web.domain.cart.CartOptionDTO;
import com.ohouse.web.domain.cart.CartOptionEditItemDTO;
import com.ohouse.web.domain.cart.CartOptionEditRequestDTO;
import com.ohouse.web.mapper.cart.CartMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.NamingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartMapper cartMapper;

    @Override
    public List<CartItemDTO> selectCartList(int cart_id) throws SQLException, NamingException {
        List<CartItemDTO> cartItems = cartMapper.selectCartList(cart_id);
        for (CartItemDTO item : cartItems) {
            List<CartOptionDTO> options = cartMapper.selectCartOptions(item.getProduct_option_id());
            item.setOptions(options);
        }
        return cartItems;
    }

    @Override
    public int findCartID(int member_id) throws SQLException, NamingException {
        return this.cartMapper.findCartID(member_id);
    }

    @Override
    public boolean insert(List<CartItemDTO> cartItemDTO, int cart_id) throws SQLException, NamingException {
        return this.cartMapper.insert(cartItemDTO, cart_id);
    }
    @Transactional
    @Override
    public void updateCartOption(int member_id, CartOptionEditRequestDTO requestDTO) throws Exception {
        // ========================================
        // 1. 회원의 cart_id 조회
        // ========================================

        int cartId = cartMapper.findCartID(member_id);


        // ========================================
        // 2. 삭제할 장바구니 상품 처리
        // ========================================

        List<Integer> deleteIds = requestDTO.getDeleted_cart_items_ids();

        if (deleteIds != null && !deleteIds.isEmpty()) {

            /*
             * 현재 장바구니에 있는 상품 조회
             */
            List<CartItemDTO> cartItems = cartMapper.selectCartList(cartId);


            /*
             * 현재 수정 중인 상품(product_id)에
             * 해당하는 cart_items_id만 추출
             */
            Set<Integer> editableItemIds = new HashSet<>();

            for (CartItemDTO cartItem : cartItems) {

                if (cartItem.getProduct_id() == requestDTO.getProduct_id()) {

                    editableItemIds.add(cartItem.getCart_items_id());
                }
            }


            /*
             * 실제 현재 상품에 속한 삭제 ID만 남김
             *
             * 다른 상품의 cart_items_id를
             * 클라이언트가 보내더라도 삭제되지 않음
             */
            List<Integer> validDeleteIds = deleteIds.stream().filter(editableItemIds::contains).collect(Collectors.toList());


            /*
             * 삭제할 항목이 있으면 삭제
             */
            if (!validDeleteIds.isEmpty()) {

                cartMapper.deleteCartItems(cartId, validDeleteIds);
            }
        }


        // ========================================
        // 3. 상품 INSERT / UPDATE
        // ========================================

        List<CartOptionEditItemDTO> items = requestDTO.getItems();
        if (items != null && !items.isEmpty()) {
            /*
             * 신규 상품을 모아둘 리스트
             */
            List<CartItemDTO> newItems = new ArrayList<>();
            for (CartOptionEditItemDTO item : items) {
                // 신규 상품
                if (item.isNew()) {
                    CartItemDTO cartItem = CartItemDTO.builder().product_option_id(item.getProduct_option_id()).quantity(item.getQuantity()).build();
                    newItems.add(cartItem);
                }
                // 기존 상품
                else {
                    /*
                     * 기존 cart_items_id가 존재하는
                     * 상품만 UPDATE
                     */
                    if (item.getCart_items_id() != null) {

                        cartMapper.updateCartOption(item,cartId);
                    }
                }
            }
            // ====================================
            // 4. 신규 상품 일괄 INSERT
            // ====================================

            if (!newItems.isEmpty()) {

                cartMapper.insert(newItems, cartId);
            }
        }
        // ========================================
        // 5. 장바구니 총액 갱신
        // ========================================

        cartMapper.updateTotalPrice(cartId);
    }


    @Override
    public int updateTotalPrice(int cart_id) throws SQLException, NamingException {
        return 0;
    }

    @Override
    public int updateCartQuantity(CartItemDTO cartItemDTO, int cart_id) throws SQLException, NamingException {

        return cartMapper.updateCartQuantity(cartItemDTO, cart_id);
    }

    @Override
    public int deleteCartItems(int cart_id, List<Integer> cartItemsIds) throws Exception {
        return this.cartMapper.deleteCartItems(cart_id, cartItemsIds);
    }
}
