package com.ohouse.web.mapper.cart;

import com.ohouse.web.domain.cart.CartItemDTO;
import com.ohouse.web.domain.cart.CartOptionDTO;
import com.ohouse.web.domain.cart.CartOptionEditItemDTO;
import org.apache.ibatis.annotations.Param;

import javax.naming.NamingException;
import java.sql.SQLException;
import java.util.List;

public interface CartMapper {
    List<CartItemDTO> selectCartList(int cart_id) throws SQLException, NamingException;

    int findCartID(int member_id) throws SQLException, NamingException;

    boolean insert(@Param("cartItemDTOList") List<CartItemDTO> cartItemDTOList, @Param("cart_id") int cart_id) throws SQLException, NamingException;

    void updateCartOption(
            @Param("item") CartOptionEditItemDTO item,
            @Param("cart_id") int cart_id
    );

    int updateCartQuantity( @Param("cartItemDTO") CartItemDTO cartItemDTO,
                             @Param("cart_id") int cart_id) throws SQLException, NamingException;

    List<CartOptionDTO> selectCartOptions(long product_option_id);

    int deleteCartItems(@Param("cart_id") int cart_id, @Param("cartItemIds") List<Integer> cartItemsIds) throws Exception;

    void updateTotalPrice(@Param("cart_id") int cart_id) throws SQLException, NamingException;
}
