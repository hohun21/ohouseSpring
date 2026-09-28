package com.ohouse.web.controller.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ohouse.web.domain.order.OrderItemDTO;
import com.ohouse.web.domain.order.OrderRequestDTO;
import com.ohouse.web.service.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

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
            Model model
    ) {
        List<OrderItemDTO> orderdto =
                (List<OrderItemDTO>) session.getAttribute("orderdto");

        model.addAttribute("orderdto", orderdto);

        return "product/order";
    }

    @PostMapping("/payment/create.htm")
    @ResponseBody
    public String orderCreate(@RequestBody String body, HttpSession session) {
        try {
            ObjectMapper om = new ObjectMapper();
            OrderRequestDTO orderRequestDTO = om.readValue(body, OrderRequestDTO.class);

            int member_id = 4;
            List<Integer> cart_items_ids = (List<Integer>) session.getAttribute("selectedCartItemsIds");
            int order_id = orderService.insertOrder(member_id, orderRequestDTO, cart_items_ids);
            session.setAttribute("order_id",order_id);
            return "{\"success\":true}";
        } catch (Exception e) {
            String msg = e.getMessage() == null ? "" : e.getMessage().replace("\"", "\\\"");
            return "{\"success\":false,\"message\":\"" + msg + "\"}";
        }
    }
    @GetMapping("/payment/success.htm")
    public String paymentSuccess(  @RequestParam("orderName") String orderName,HttpSession session,Model model) {
        int order_id = (Integer) session.getAttribute("order_id");
        model.addAttribute("orderName", orderName);
        model.addAttribute("order_id", order_id);
        return "/payment/success";
    }

}
