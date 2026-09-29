package com.ohouse.web.controller.mypage;

import com.ohouse.web.domain.member.CouponDTO;
import com.ohouse.web.domain.member.MyOrderDTO;
import com.ohouse.web.domain.order.OrderStatusCountDTO;
import com.ohouse.web.service.member.MemberService;
import com.ohouse.web.service.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
public class MyPageController {
    private final MemberService memberService;
    private final OrderService orderService;
    @GetMapping("/myPage.htm")
    public String myPage(@AuthenticationPrincipal Object principal, Model model) {
        
        if (principal == null) {
            return "redirect:/auth/login.htm";
        }

        return "member/mypage"; 
    }
    @GetMapping("/myShopping.htm")
    public String myShopping(Model model,
                             @AuthenticationPrincipal(expression = "member_vo.memberId") Integer member_id) throws Exception {

        List<MyOrderDTO> orderdto = this.memberService.selectorder(member_id);
        OrderStatusCountDTO ordercount = this.orderService.getOrderStatusCount(member_id);
        int couponCount = this.orderService.getCouponCount(member_id);
        model.addAttribute("orderdto",orderdto);
        model.addAttribute("statusCount",ordercount);
        model.addAttribute("couponCount",couponCount);
        return "member/myShopping";
    }
    // 쿠폰
    @GetMapping("/couponlist.htm")
    public String couponlist(Model model,
                             @AuthenticationPrincipal(expression = "member_vo.memberId") Integer member_id) throws Exception {
        List<CouponDTO> clist = this.memberService.selectCoupon(member_id);
        model.addAttribute("clist",clist);
        return "member/couponlist";
    }

}