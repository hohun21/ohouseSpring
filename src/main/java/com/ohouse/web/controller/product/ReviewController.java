package com.ohouse.web.controller.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ohouse.web.controller.main.MainController;
import com.ohouse.web.mapper.product.ReviewMapper;
import com.ohouse.web.service.product.ReviewService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;

@Controller
@Log4j
@RequestMapping("/product/review")
@RequiredArgsConstructor
public class ReviewController {
	
	@Autowired
    private ReviewService reviewService;
	
	@GetMapping(value = "/reviewList.htm")
	public String getReviewList() {
		
		log.info("🤩 MainController.main()...");
				
		 return "/reviewList";
		
	}
}
