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
import com.ohouse.web.service.member.MemberService;
import com.ohouse.web.service.order.OrderService;
import com.ohouse.web.service.product.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    private final ReviewService reviewService;

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


    @GetMapping("/myReview.htm")
    public String myReview(
            @RequestParam(value = "page", defaultValue = "1") int currentPage,
            @RequestParam(value = "sort", defaultValue = "recent") String sort,
            @RequestParam(value = "ajax", required = false) String ajaxParam,
            Model model,
            HttpServletRequest request,
            Authentication authentication,
            @AuthenticationPrincipal(expression = "member_vo.memberId") Integer member_id) throws Exception {

		/*
		 * // 1. 세션에서 로그인 사용자 정보 확인 (기존 핸들러 로직 그대로 반영) AuthUserDTO authUser =
		 * (AuthUserDTO) session.getAttribute("authUser"); Object sellerAuth =
		 * session.getAttribute("sellerAuth");
		 * 
		 * if (authUser == null && sellerAuth == null) { return "redirect:/login.htm"; }
		 */

        int memberId = member_id;
        int numberPerPage = 5;

		boolean isAdmin = false;

		if (authentication != null && authentication.getPrincipal() instanceof CustomerUser) {
		    CustomerUser customerUser = (CustomerUser) authentication.getPrincipal();
		    memberId = customerUser.getMember_vo().getMemberId();
		    
		    isAdmin = authentication.getAuthorities().stream()
		                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
		                
		    
		}
        
        // 2. ReviewPageDTO 빌드 (DTO 수정 없이 필드명 맞춤)
        ReviewPageDTO reqDTO = ReviewPageDTO.builder()
                .memberId(memberId)
                .sort(sort)
                .currentPage(currentPage)
                .numberPerPage(numberPerPage)
                .build();

        // 3. 마이페이지 전용 서비스 메서드 호출 (0건 뜨던 원인 해결)
        int totalRecords = reviewService.selectMyReviewTotalCount(memberId);
        PageDTO pageDTO = new PageDTO(totalRecords, currentPage, numberPerPage);
        List<ReviewDTO> reviewList = reviewService.selectMyReviewList(reqDTO);

        // 4. Model에 데이터 담기 (JSP의 request.setAttribute 대체)
        model.addAttribute("reviewList", reviewList);
        model.addAttribute("pageDTO", pageDTO);
        model.addAttribute("currentSort", sort);
        model.addAttribute("isAdmin", isAdmin);

        // 5. AJAX 요청 여부 확인 (파라미터 또는 헤더 체크)
        boolean isAjax = "true".equals(ajaxParam) || "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));

        if (isAjax) {
            return "member/ajaxMyReview"; // 스프링 뷰 리졸버 설정에 따라 접두사/접미사 자동 붙음 (예: /WEB-INF/views/member/ajaxMyReview.jsp)
        }

        return "member/myReview"; // /WEB-INF/views/member/myReview.jsp
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