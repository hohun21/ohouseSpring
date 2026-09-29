package com.ohouse.web.controller.mypage;

import com.ohouse.web.domain.member.CouponDTO;
import com.ohouse.web.domain.member.MyOrderDTO;
import com.ohouse.web.domain.address.ShippingAddressDTO;
import com.ohouse.web.service.member.MemberService;
import com.ohouse.web.service.address.AddressService;
import com.ohouse.web.domain.order.OrderStatusCountDTO;
import com.ohouse.web.service.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
public class MyPageController {
    
    private final MemberService memberService;
    private final AddressService addressService; // 💡 배송지 서비스 추가
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

    
    @GetMapping("/addressList.htm")
    public String addressList(Model model, 
                              @AuthenticationPrincipal(expression = "member_vo.memberId") Integer member_id) {
        
        List<ShippingAddressDTO> addressList = addressService.getAddressList(member_id);
        model.addAttribute("addressList", addressList);
        
        return "member/addressList"; // /WEB-INF/views/member/addressList.jsp 로 포워딩
    }

    @PostMapping(value = "/addAddress.htm", produces = "text/html; charset=UTF-8")
    @ResponseBody
    public String addAddress(ShippingAddressDTO dto,
                             @AuthenticationPrincipal(expression = "member_vo.memberId") Integer member_id,
                             @RequestParam(value = "is_default", required = false) String isDefault) {
        
        dto.setMember_id(member_id);
        dto.setIs_default(isDefault != null ? "Y" : "N");
        
        int result = addressService.addShippingAddress(dto);
        
        StringBuilder sb = new StringBuilder("<script>");
        if (result == -1) {
            sb.append("alert('배송지는 최대 3개까지만 등록할 수 있습니다.'); history.back();");
        } else if (result > 0) {
            sb.append("alert('배송지가 성공적으로 등록되었습니다.'); location.href='addressList.htm?openModal=true';");
        } else {
            sb.append("alert('등록에 실패했습니다. 다시 시도해주세요.'); history.back();");
        }
        sb.append("</script>");
        
        return sb.toString(); 
    }

    @RequestMapping(value = "/deleteAddress.htm", produces = "text/html; charset=UTF-8")
    @ResponseBody
    public String deleteAddress(@RequestParam("address_id") int addressId,
                                @AuthenticationPrincipal(expression = "member_vo.memberId") Integer member_id) {
        
        addressService.deleteAddress(addressId, member_id);
        
        return "<script>alert('배송지가 삭제되었습니다.'); location.href='addressList.htm?openModal=true';</script>";
    }

    @RequestMapping(value = "/setDefaultAddress.htm", produces = "text/html; charset=UTF-8")
    @ResponseBody
    public String setDefaultAddress(@RequestParam("address_id") int addressId,
                                    @AuthenticationPrincipal(expression = "member_vo.memberId") Integer member_id) {
        
        addressService.setDefaultAddress(addressId, member_id);
        
        return "<script>alert('기본배송지가 변경되었습니다.'); location.href='addressList.htm?openModal=true';</script>";
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