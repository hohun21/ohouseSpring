package com.ohouse.web.controller.mypage;

import com.ohouse.web.domain.address.ShippingAddressDTO;
import com.ohouse.web.domain.member.CouponDTO;
import com.ohouse.web.domain.member.MyOrderDTO;
import com.ohouse.web.domain.order.OrderStatusCountDTO;
import com.ohouse.web.domain.product.review.PageDTO;
import com.ohouse.web.domain.product.review.ReviewDTO;
import com.ohouse.web.domain.product.review.ReviewPageDTO;
import com.ohouse.web.domain.security.CustomerUser;
import com.ohouse.web.service.address.AddressService;
import com.ohouse.web.service.auth.MemberAuthService;
import com.ohouse.web.service.member.MemberService;
import com.ohouse.web.service.order.OrderService;
import com.ohouse.web.service.product.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
public class MyPageController {

    private final MemberService memberService;
    private final MemberAuthService memberAuthService;
    private final ReviewService reviewService;
    private final AddressService addressService;
    private final OrderService orderService;

    private Integer getCustomerMemberId(Object principal) {
        if (principal instanceof CustomerUser) {
            return ((CustomerUser) principal).getMember_vo().getMemberId();
        }
        return null;
    }

    @GetMapping("/myPage.htm")
    public String myPage(@AuthenticationPrincipal Object principal, Model model) {
        if (principal == null) {
            return "redirect:/auth/login.htm";
        }
        return "member/mypage"; 
    }

    @GetMapping("/myShopping.htm")
    public String myShopping(Model model, @AuthenticationPrincipal Object principal) throws Exception {
        Integer memberId = getCustomerMemberId(principal);
        if (memberId == null) return "redirect:/"; // 판매자가 직접 URL 쳤을 때 홈으로 튕겨냄

        List<MyOrderDTO> orderdto = this.memberService.selectorder(memberId);
        OrderStatusCountDTO ordercount = this.orderService.getOrderStatusCount(memberId);
        int couponCount = this.orderService.getCouponCount(memberId);
        
        model.addAttribute("orderdto", orderdto);
        model.addAttribute("statusCount", ordercount);
        model.addAttribute("couponCount", couponCount);
        return "member/myShopping";
    }

    @GetMapping("/myReview.htm")
    public String myReview(
            @RequestParam(value = "page", defaultValue = "1") int currentPage,
            @RequestParam(value = "sort", defaultValue = "recent") String sort,
            @RequestParam(value = "ajax", required = false) String ajaxParam,
            Model model,
            HttpServletRequest request,
            Authentication authentication,
            @AuthenticationPrincipal Object principal) throws Exception {

        Integer memberId = getCustomerMemberId(principal);
        if (memberId == null) return "redirect:/"; // 판매자 접근 차단

        int numberPerPage = 5;
        boolean isAdmin = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        
        // 2. ReviewPageDTO 빌드
        ReviewPageDTO reqDTO = ReviewPageDTO.builder()
                .memberId(memberId)
                .sort(sort)
                .currentPage(currentPage)
                .numberPerPage(numberPerPage)
                .build();

        // 3. 마이페이지 전용 서비스 메서드 호출
        int totalRecords = reviewService.selectMyReviewTotalCount(memberId);
        PageDTO pageDTO = new PageDTO(totalRecords, currentPage, numberPerPage);
        List<ReviewDTO> reviewList = reviewService.selectMyReviewList(reqDTO);

        // 4. Model에 데이터 담기
        model.addAttribute("reviewList", reviewList);
        model.addAttribute("pageDTO", pageDTO);
        model.addAttribute("currentSort", sort);
        model.addAttribute("isAdmin", isAdmin);

        // 5. AJAX 요청 여부 확인
        boolean isAjax = "true".equals(ajaxParam) || "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));

        if (isAjax) {
            return "member/ajaxMyReview"; 
        }

        return "member/myReview";
    }

    @GetMapping("/addressList.htm")
    public String addressList(Model model, @AuthenticationPrincipal Object principal) {
        Integer memberId = getCustomerMemberId(principal);
        if (memberId == null) return "redirect:/";

        List<ShippingAddressDTO> addressList = addressService.getAddressList(memberId);
        model.addAttribute("addressList", addressList);
        
        return "member/addressList";
    }

    @PostMapping(value = "/addAddress.htm", produces = "text/html; charset=UTF-8")
    @ResponseBody
    public String addAddress(ShippingAddressDTO dto,
                             @AuthenticationPrincipal Object principal,
                             @RequestParam(value = "is_default", required = false) String isDefault) {
        
        Integer memberId = getCustomerMemberId(principal);
        if (memberId == null) return "<script>alert('일반 회원만 이용 가능합니다.'); history.back();</script>";

        dto.setMember_id(memberId);
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
                                @AuthenticationPrincipal Object principal) {
        
        Integer memberId = getCustomerMemberId(principal);
        if (memberId == null) return "<script>alert('일반 회원만 이용 가능합니다.'); history.back();</script>";

        addressService.deleteAddress(addressId, memberId);
        
        return "<script>alert('배송지가 삭제되었습니다.'); location.href='addressList.htm?openModal=true';</script>";
    }

    @RequestMapping(value = "/setDefaultAddress.htm", produces = "text/html; charset=UTF-8")
    @ResponseBody
    public String setDefaultAddress(@RequestParam("address_id") int addressId,
                                    @AuthenticationPrincipal Object principal) {
        
        Integer memberId = getCustomerMemberId(principal);
        if (memberId == null) return "<script>alert('일반 회원만 이용 가능합니다.'); history.back();</script>";

        addressService.setDefaultAddress(addressId, memberId);
        
        return "<script>alert('기본배송지가 변경되었습니다.'); location.href='addressList.htm?openModal=true';</script>";
    }

    // 쿠폰
    @GetMapping("/couponlist.htm")
    public String couponlist(Model model, @AuthenticationPrincipal Object principal) throws Exception {
        Integer memberId = getCustomerMemberId(principal);
        if (memberId == null) return "redirect:/";

        List<CouponDTO> clist = this.memberService.selectCoupon(memberId);
        model.addAttribute("clist", clist);
        return "member/couponlist";
    }

    @GetMapping("/withdraw.htm")
    public String withdraw() {
        return "member/withdraw";
    }

    @PostMapping("/withdrawPro.htm")
    public String withdrawPro(Authentication authentication, HttpServletRequest request) {

        String id = authentication.getName();

        // db status=0 탈퇴상태변경
        memberAuthService.withdraw(id);
        
        // 로그인 세션 종료 
        request.getSession().invalidate();
        
        // Spring Security 인증 정보 제거
        SecurityContextHolder.clearContext();

        return "redirect:/main.htm";
    }

	
}