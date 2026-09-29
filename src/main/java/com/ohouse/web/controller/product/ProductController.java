package com.ohouse.web.controller.product;


import java.security.Principal;
import java.sql.SQLException;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ohouse.web.domain.product.ProductDetailDTO;
import com.ohouse.web.domain.product.ProductOptionDTO;
import com.ohouse.web.domain.product.review.OptionFilterDTO;
import com.ohouse.web.domain.product.review.PageDTO;
import com.ohouse.web.domain.product.review.ReviewDTO;
import com.ohouse.web.domain.product.review.ReviewPageDTO;
import com.ohouse.web.domain.product.review.ReviewSummaryDTO;
import com.ohouse.web.domain.security.CustomerUser;
import com.ohouse.web.service.product.ProductService;
import com.ohouse.web.service.product.ReviewService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;


@Controller
@RequestMapping(value="/product")
@RequiredArgsConstructor
@Log4j
public class ProductController {

	private final ProductService productService;
	private ObjectMapper objectMapper = new ObjectMapper();
	private final ReviewService reviewService;
	@GetMapping(value="/productDetail.htm")
	public String productDetail(Model model, 
			@RequestParam("product_id") long product_id,
			Principal principal,
			Authentication authentication)
					throws SQLException, ClassNotFoundException {
		
		ProductDetailDTO pdto = productService.productDetail(product_id);
		model.addAttribute("pdto", pdto);



		//-------------------리뷰 파트 ------------------------

		log.info("productDetail hosted - product_id: " + product_id);

		int memberId = 0;
		boolean isAdmin = false;

		// 
		if (authentication != null && authentication.getPrincipal() instanceof CustomerUser) {
		    CustomerUser customerUser = (CustomerUser) authentication.getPrincipal();
		    memberId = customerUser.getMember_vo().getMemberId();
		    
		    isAdmin = authentication.getAuthorities().stream()
		                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
		                
		    log.info("❤️❤️❤️❤️❤️❤️❤️❤️Memberid " + memberId);
		}
		int currentPage = 1;
		int numberPerPage = 5;

		// 2. 리뷰 관련 DTO 조립 (초기 진입 시 기본 'best' 정렬 기준)
		ReviewPageDTO reqDTO = ReviewPageDTO.builder()
				.productId(product_id)
				.currentPage(currentPage)
				.numberPerPage(numberPerPage)
				.sort("best")
				.memberId(memberId)
				.build();

		// 3. 리뷰 서비스 데이터 조회
		List<ReviewDTO> reviewList = reviewService.getReviewList(reqDTO);
		ReviewSummaryDTO reviewSummary = reviewService.getReviewSummary(product_id);
		int totalRecords = reviewService.getTotalRecords(reqDTO);
		List<OptionFilterDTO> optionFilterList = reviewService.getOptionFilterList(product_id);
		PageDTO pageDTO = new PageDTO(totalRecords, currentPage, numberPerPage);

		// 4. 상세 페이지 및 내부 JSP(reviewList.jsp)에서 사용할 데이터 Model에 모두 담기
		model.addAttribute("product_id", product_id);
		model.addAttribute("reviewList", reviewList);
		model.addAttribute("reviewSummary", reviewSummary);
		model.addAttribute("pageDTO", pageDTO);
		model.addAttribute("currentSort", "best");
		model.addAttribute("optionFilterList", optionFilterList);


		return "product/product_detail";
	}
	@GetMapping("/productOption.htm")
	@ResponseBody
	public ResponseEntity<String> productOption(
			@RequestParam("product_id") long product_id,
			@RequestParam("option_value_ids") List<Long> option_value_ids
			) throws SQLException, JsonProcessingException {


		ProductOptionDTO result =
				productService.productOption(product_id, option_value_ids);

		System.out.println("========== PRODUCT OPTION ==========");
		System.out.println("result = " + result);
		System.out.println("brand_name = " + result.getBrand_name());
		System.out.println("product_option_id = " + result.getProduct_option_id());
		System.out.println("===================================");


		String json = objectMapper.writeValueAsString(result);


		System.out.println("========== JSON BEFORE RETURN ==========");
		System.out.println(json);
		System.out.println("========================================");

        return ResponseEntity
                .ok()
                .contentType(MediaType.parseMediaType("text/plain;charset=UTF-8"))
                .body(json);
    }


}
