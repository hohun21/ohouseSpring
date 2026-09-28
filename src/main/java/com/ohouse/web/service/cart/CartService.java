package com.ohouse.web.service.cart;

import com.ohouse.web.domain.cart.CartItemDTO;
import com.ohouse.web.domain.cart.CartOptionEditRequestDTO;
import org.apache.ibatis.annotations.Param;

import javax.naming.NamingException;
import java.sql.SQLException;
import java.util.List;

public interface CartService {

    int findCartID(@Param("member_id") int member_id) throws SQLException, NamingException;

    List<CartItemDTO> selectCartList(int cart_id) throws SQLException, NamingException;

    boolean insert(List<CartItemDTO> cartItemDTO, int member_id) throws SQLException, NamingException;

//    List<CartOptionDTO> selectCartOption(List<Long> productIds) throws SQLException, NamingException;

    void updateCartOption(int cart_id, CartOptionEditRequestDTO requestDTO) throws Exception;

    int updateCartQuantity(CartItemDTO cartItemDTO, int cart_id) throws SQLException, NamingException;

//    CartDTO selectCart(int member_id) throws SQLException, NamingException;

    int updateTotalPrice(int cart_id) throws SQLException, NamingException;

    int deleteCartItems( int cart_id, List<Integer> cartItemsIds) throws Exception;

    //List<CartOptionDTO> selectCartOptions(long product_option_id) throws SQLException;

    //void createCart(int member_id) throws SQLException;

}
