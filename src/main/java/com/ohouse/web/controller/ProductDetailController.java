package com.ohouse.web.controller;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ohouse.member.dto.AuthUserDTO;
import com.ohouse.web.domain.product.review.OptionFilterDTO;
import com.ohouse.web.domain.product.review.PageDTO;
import com.ohouse.web.domain.product.review.ReviewDTO;
import com.ohouse.web.domain.product.review.ReviewPageDTO;
import com.ohouse.web.domain.product.review.ReviewSummaryDTO;
import com.ohouse.web.service.product.ReviewService;

import lombok.extern.log4j.Log4j;

@Controller
@Log4j
public class ProductDetailController {
	@Autowired
    private ReviewService reviewService;
	
	
    // 브라우저 주소창에 http://localhost:8080/product/productDetail.htm?product_id=1 치면 들어가지도록 매핑
    @GetMapping("/product/productDetail.htm")
    public String productDetail(@RequestParam(value = "product_id", defaultValue = "1") int productId, Model model) throws ClassNotFoundException, SQLException {
        
        // 상세 페이지에서 쓸 상품 번호를 모델에 담아줍니다.
        model.addAttribute("product_id", productId);
        
        
        //-------------------리뷰 파트 ------------------------
        
        log.info("productDetail hosted - product_id: " + productId);
        
        // 1. 사용자 인증 정보 세팅 (리뷰 작성자 체크 및 관리자 여부 확인용)
        AuthUserDTO authUser = new AuthUserDTO(1, "ADMIN","관리자","role");//(AuthUserDTO) session.getAttribute("authUser");
        int memberId = 0;
        String role = "";
        
        if(authUser != null) {
            memberId = authUser.getMemberId();
            role = authUser.getRole();
        }
        boolean isAdmin = "ADMIN".equals(role);

        int currentPage = 1;
        int numberPerPage = 5;
        
        // 2. 리뷰 관련 DTO 조립 (초기 진입 시 기본 'best' 정렬 기준)
        ReviewPageDTO reqDTO = ReviewPageDTO.builder()
                .productId(productId)
                .currentPage(currentPage)
                .numberPerPage(numberPerPage)
                .sort("best")
                .memberId(memberId)
                .build();

        // 3. 리뷰 서비스 데이터 조회
        List<ReviewDTO> reviewList = reviewService.getReviewList(reqDTO);
        ReviewSummaryDTO reviewSummary = reviewService.getReviewSummary(productId);
        int totalRecords = reviewService.getTotalRecords(reqDTO);
        List<OptionFilterDTO> optionFilterList = reviewService.getOptionFilterList(productId);
        PageDTO pageDTO = new PageDTO(totalRecords, currentPage, numberPerPage);

        // 4. 상세 페이지 및 내부 JSP(reviewList.jsp)에서 사용할 데이터 Model에 모두 담기
        model.addAttribute("product_id", productId);
        model.addAttribute("reviewList", reviewList);
        model.addAttribute("reviewSummary", reviewSummary);
        model.addAttribute("pageDTO", pageDTO);
        model.addAttribute("currentSort", "best");
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("optionFilterList", optionFilterList);
        
        // src/main/webapp/WEB-INF/views/product/productDetail.jsp (또는 본인 프로젝트의 상세페이지 경로)로 이동
        return "product/product_detail"; 
    }
}