package com.ohouse.web.controller.order;

import com.ohouse.web.domain.address.ShippingAddressDTO;
import com.ohouse.web.domain.member.CouponDTO;
import com.ohouse.web.domain.order.OrderItemDTO;
import com.ohouse.web.domain.order.OrderRequestDTO;
import com.ohouse.web.service.address.AddressService;
import com.ohouse.web.service.member.MemberService;
import com.ohouse.web.service.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.naming.NamingException;
import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.List;

@Controller
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final MemberService memberService;
    private final AddressService addressService;

    @PostMapping("/productOrder.htm")
    public ResponseEntity<String> productOrder(
            @RequestBody List<OrderItemDTO> orderdto,
            HttpSession session
            ){
        for (OrderItemDTO item : orderdto) {
            System.out.println("brand_id = " + item.getBrand_id());
            System.out.println("brand_name = " + item.getBrand_name());
            System.out.println("product_id = " + item.getProduct_id());
            System.out.println("product_name = " + item.getProduct_name());
            System.out.println("price = " + item.getPrice());
            System.out.println("image_url = " + item.getImage_url());
        }
        session.setAttribute("orderdto",orderdto);
        return ResponseEntity.ok("success");
    }

    @GetMapping("/order.htm")
    public String order(
            HttpSession session,
            Model model,
            @AuthenticationPrincipal(expression = "member_vo.memberId") Integer member_id
    ) throws SQLException {
        List<OrderItemDTO> orderdto =
                (List<OrderItemDTO>) session.getAttribute("orderdto");
        List<CouponDTO> clist = this.memberService.selectCoupon(member_id);
        List<ShippingAddressDTO> addressList = this.addressService.getAddressList(member_id);
        model.addAttribute("orderdto", orderdto);
        model.addAttribute("clist", clist);
        model.addAttribute("addressList", addressList);
        return "product/order";
    }

    @PostMapping("/payment/create.htm")
    @ResponseBody
    public String orderCreate(@RequestBody OrderRequestDTO orderRequestDTO,
                              HttpSession session,
                              @AuthenticationPrincipal(expression = "member_vo.memberId") Integer member_id) {
        try {

            List<Integer> cart_items_ids = (List<Integer>) session.getAttribute("selectedCartItemsIds");

            this.orderService.insertOrder(member_id,orderRequestDTO,cart_items_ids);
            return "{\"success\":true}";
        } catch (Exception e) {
            String msg = e.getMessage() == null ? "" : e.getMessage().replace("\"", "\\\"");
            return "{\"success\":false,\"message\":\"" + msg + "\"}";
        }
    }
    @GetMapping("/payment/success.htm")
    public String paymentSuccess(
            @RequestParam("orderName") String orderName,
            @RequestParam("tossOrderId") String tossOrderId,
            HttpSession session,
            Model model) {

        model.addAttribute("orderName", orderName);
        model.addAttribute("tossOrderId", tossOrderId);

        return "/product/ordersuccess";
    }
    @PostMapping("/payment/confirm.htm")
    public String paymentConfirm(@RequestParam("orders_detail_id") int orders_detail_id) throws SQLException, NamingException {
        this.orderService.payconfirm(orders_detail_id);
        return "redirect:/member/myShopping.htm";
    }
    @PostMapping("/payment/cancel.htm")
    public String paymentCancel(@RequestParam("orders_detail_id") int orders_detail_id) throws SQLException, NamingException {
        this.orderService.paycancel(orders_detail_id);
        return "redirect:/member/myShopping.htm";
    }
    @PostMapping("/payment/return.htm")
    public String paymentReturn(@RequestParam("orders_detail_id") int orders_detail_id) throws SQLException, NamingException {
        this.orderService.payreturn(orders_detail_id);
        return "redirect:/member/myShopping.htm";
    }
}
