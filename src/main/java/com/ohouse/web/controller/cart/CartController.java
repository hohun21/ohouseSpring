package com.ohouse.web.controller.cart;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ohouse.web.domain.cart.CartItemDTO;
import com.ohouse.web.domain.cart.CartOptionEditRequestDTO;
import com.ohouse.web.service.cart.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.naming.NamingException;
import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;
    private ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/cart.htm")
    public String getCart(
            @AuthenticationPrincipal(expression = "member_vo.memberId") Integer member_id,
            Model model) throws SQLException, NamingException {


        int cart_id = this.cartService.findCartID(member_id);

        List<CartItemDTO> cdto = this.cartService.selectCartList(cart_id);

        model.addAttribute("cdto", cdto);
        return "product/cart";
    }

    @PostMapping("/cartAdd.htm")
    @ResponseStatus(HttpStatus.OK)
    public void cartAdd(@RequestBody List<CartItemDTO> cartItemDTOList,
                        @AuthenticationPrincipal(expression = "member_vo.memberId") int member_id)
            throws SQLException, NamingException {

        int cart_id = this.cartService.findCartID(member_id);
        cartService.insert(cartItemDTOList, cart_id);
    }

    @PostMapping("/cartQuantityEdit.htm")
    @ResponseStatus(HttpStatus.OK)
    public void cartQuantityEdit(@RequestBody CartItemDTO cartItemDTO,
                                 @AuthenticationPrincipal(expression = "member_vo.memberId") int member_id)
            throws SQLException, NamingException {

        int cart_id = cartService.findCartID(member_id);

        cartService.updateCartQuantity(cartItemDTO, cart_id);
    }

    @PostMapping("/cartOptionEdit.htm")
    @ResponseStatus(HttpStatus.OK)
    public void cartOptionEdit(
            @RequestBody CartOptionEditRequestDTO requestDTO,
            @AuthenticationPrincipal(expression = "member_vo.memberId") int member_id
    ) throws Exception {

        cartService.updateCartOption(member_id,requestDTO);
    }

    @PostMapping("/cartDelete.htm")
    public ResponseEntity<String> cartDelete(@RequestBody List<Integer> cartItemsIds,
                                             @AuthenticationPrincipal(expression = "member_vo.memberId") int member_id)
            throws Exception {

        int cart_id = this.cartService.findCartID(member_id);

        int result = this.cartService.deleteCartItems(cart_id, cartItemsIds);
        return ResponseEntity.ok(String.valueOf(result));
    }

    @PostMapping("/cartOrder.htm")
    public ResponseEntity<String> cartOrder(@RequestBody List<CartItemDTO> cartItemDTOList, HttpSession session) {
        List<Integer> cartItemsIds = cartItemDTOList.stream().map(CartItemDTO::getCart_items_id).collect(Collectors.toList());

        session.setAttribute("orderdto", cartItemDTOList);
        session.setAttribute("selectedCartItemsIds", cartItemsIds);

        return ResponseEntity.ok("success");
    }


}
