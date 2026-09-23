package com.ohouse.web.controller.admin;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ohouse.web.domain.admin.CouponDTO;
import com.ohouse.web.domain.member.MemberDTO;
import com.ohouse.web.domain.seller.ProductDTO;
import com.ohouse.web.domain.seller.SellerDTO;
import com.ohouse.web.domain.seller.SellerOrderDTO;
import com.ohouse.web.service.admin.AdminService;
import com.ohouse.web.service.admin.AdminSettlementService;
import com.ohouse.web.service.admin.CouponService;
import com.ohouse.web.service.seller.SellerService;

@Controller
@RequestMapping("/admin")
public class AdminController {
    
    @Autowired
    private AdminService adminService;

    @Autowired
    private AdminSettlementService settlementService;

    @Autowired
    private CouponService couponService;

    @Autowired
    private SellerService sellerService;

    // 1. 대시보드
    @GetMapping("/dashboard.htm")
    public String dashboard() {
        return "admin/admin_dashboard";
    }

    // 2. 회원 관리
    @GetMapping("/memberList.htm")
    public String memberList(
            @RequestParam(value = "page", required = false, defaultValue = "1") int currentPage,
            Model model
    ) {
        int pageSize = 15;
        int pageBlock = 10;
        
        int startRow = (currentPage - 1) * pageSize + 1;
        int endRow = currentPage * pageSize;
        
        int totalCount = adminService.getTotalMemberCount();
        List<MemberDTO> memberList = adminService.getMemberListWithPaging(startRow, endRow);
        
        int totalPage = (int) Math.ceil((double) totalCount / pageSize);
        int startPage = ((currentPage - 1) / pageBlock) * pageBlock + 1;
        int endPage = startPage + pageBlock - 1;
        if (endPage > totalPage) endPage = totalPage;
        
        model.addAttribute("memberList", memberList);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPage", totalPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        
        return "admin/all_member_list";
    }

    @GetMapping("/memberStatusUpdate.htm")
    public String memberStatusUpdate(
            @RequestParam(value = "member_id", required = false) String memberIdStr,
            @RequestParam(value = "status", required = false) String statusStr
    ) {
        if (memberIdStr != null && !memberIdStr.isEmpty() && statusStr != null && !statusStr.isEmpty()) {
            int memberId = Integer.parseInt(memberIdStr);
            int status = Integer.parseInt(statusStr);
            adminService.changeMemberStatus(memberId, status);
        }
        return "redirect:/admin/memberList.htm";
    }

    // 3. 판매자 관리
    @GetMapping("/sellerList.htm")
    public String sellerList(
            @RequestParam(value = "page", required = false, defaultValue = "1") int currentPage,
            Model model
    ) {
        int pageSize = 15;
        int pageBlock = 10;
        
        int startRow = (currentPage - 1) * pageSize + 1;
        int endRow = currentPage * pageSize;
        
        int totalCount = adminService.getTotalSellerCount();
        List<SellerDTO> sellerList = adminService.getSellerListWithPaging(startRow, endRow);
        
        int totalPage = (int) Math.ceil((double) totalCount / pageSize);
        int startPage = ((currentPage - 1) / pageBlock) * pageBlock + 1;
        int endPage = startPage + pageBlock - 1;
        if (endPage > totalPage) endPage = totalPage;
        
        model.addAttribute("sellerList", sellerList);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPage", totalPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        
        return "admin/seller_list";
    }

    @GetMapping("/pendingSellers.htm")
    public String pendingSellers(
            @RequestParam(value = "page", required = false, defaultValue = "1") int currentPage,
            Model model
    ) {
        int pageSize = 12;
        int pageBlock = 10;
        
        int startRow = (currentPage - 1) * pageSize + 1;
        int endRow = currentPage * pageSize;
        
        int totalCount = adminService.getPendingSellerCount();
        List<SellerDTO> pendingList = adminService.getPendingSellersWithPaging(startRow, endRow);
        
        int totalPage = (int) Math.ceil((double) totalCount / pageSize);
        int startPage = ((currentPage - 1) / pageBlock) * pageBlock + 1;
        int endPage = startPage + pageBlock - 1;
        if (endPage > totalPage) endPage = totalPage;
        
        model.addAttribute("pendingList", pendingList);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPage", totalPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        
        return "admin/pending_seller_list";
    }

    @GetMapping("/approveSeller.htm")
    public String approveSeller(
            @RequestParam("sellerId") int sellerId,
            @RequestParam("action") String action
    ) {
        String status = "PENDING";
        if ("approve".equals(action)) {
            status = "ACTIVE";
        } else if ("reject".equals(action)) {
            status = "REJECTED";
        }
        
        adminService.updateSellerStatus(sellerId, status);
        return "redirect:/admin/pendingSellers.htm";
    }

    @GetMapping("/sellerStatusUpdate.htm")
    public String sellerStatusUpdate(
            @RequestParam(value = "seller_id", required = false) String sellerIdStr,
            @RequestParam(value = "status", required = false) String status
    ) {
        if (sellerIdStr != null && !sellerIdStr.isEmpty() && status != null && !status.isEmpty()) {
            int sellerId = Integer.parseInt(sellerIdStr);
            sellerService.updateSellerStatus(sellerId, status);
        }
        return "redirect:/admin/sellerList.htm";
    }

    // 4. 상품 관리
    @GetMapping("/productList.htm")
    public String productList(Model model) {
        List<ProductDTO> adminProductList = adminService.getAllProductsForAdmin();
        model.addAttribute("adminProductList", adminProductList);
        return "admin/admin_product_list";
    }

    @GetMapping("/deleteProduct.htm")
    public String deleteProduct(@RequestParam("productId") int productId) {
        adminService.deleteProductByAdmin(productId);
        return "redirect:/admin/productList.htm";
    }

    @GetMapping("/productStatus.htm")
    public String productStatus(
            @RequestParam("productId") int productId,
            @RequestParam("status") String status
    ) {
        boolean isSuccess = sellerService.updateProductStatus(productId, status);
        if (isSuccess) {
            return "redirect:/admin/productList.htm";
        } else {
            return "redirect:/admin/productList.htm?error=statusUpdateFailed";
        }
    }

    // 5. 정산 관리
    @GetMapping("/settlementList.htm")
    public String settlementList(Model model) {
        List<SellerOrderDTO> settlementList = settlementService.getAdminSettlementList();
        model.addAttribute("settlementList", settlementList);
        return "admin/settlement_list";
    }

    @GetMapping("/executeSettlement.htm")
    @ResponseBody
    public String executeSettlement(@RequestParam("orderDetailId") int orderDetailId) {
        boolean success = settlementService.executeSettlement(orderDetailId);

        if (success) {
            return "<script>alert('정산이 완료되었습니다.'); location.href='/admin/settlementList.htm';</script>";
        } else {
            return "<script>alert('정산 처리에 실패했습니다.'); history.back();</script>";
        }
    }

    // 6. 쿠폰 관리
    @GetMapping("/couponList.htm")
    public String couponList(Model model) throws Exception {
        List<CouponDTO> couponList = couponService.getCouponList();
        model.addAttribute("couponList", couponList);
        return "admin/coupon_list";
    }

    @GetMapping("/addCoupon.htm")
    public String addCouponForm() {
        return "admin/addCoupon";
    }
    
    @PostMapping("/addCoupon.htm")
    public String addCouponPro(
            @RequestParam("couponName") String couponName,
            @RequestParam("discountType") String discountType,
            @RequestParam("discountValue") int discountValue,
            @RequestParam("minOrderPrice") int minOrderPrice,
            @RequestParam("maxDiscount") Integer maxDiscount,
            @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate
    ) throws Exception {

        CouponDTO coupon = CouponDTO.builder()
                .couponName(couponName)
                .discountType(discountType)
                .discountValue(discountValue)
                .minOrderPrice(minOrderPrice)
                .maxDiscount(maxDiscount)
                .endDate(endDate)
                .build();

        couponService.addCoupon(coupon);

        return "redirect:/admin/couponList.htm";
    }

    @GetMapping("/couponStatusUpdate.htm")
    public String couponStatusUpdate(
            @RequestParam("couponId") int couponId,
            @RequestParam("status") int status
    ) throws Exception {
        couponService.updateCouponStatus(couponId, status);
        return "redirect:/admin/couponList.htm";
    }

    @GetMapping("/issueCoupon.htm")
    public String issueCoupon(@RequestParam("couponId") int couponId) throws Exception {
        couponService.issueCouponToAll(couponId);
        return "redirect:/admin/couponList.htm";
    }
}