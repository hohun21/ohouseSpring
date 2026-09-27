package com.ohouse.web.controller.product;

import java.io.PrintWriter;
import java.net.URI;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.ohouse.member.dto.AuthUserDTO;
import com.ohouse.web.domain.product.review.OptionFilterDTO;
import com.ohouse.web.domain.product.review.PageDTO;
import com.ohouse.web.domain.product.review.ReviewDTO;
import com.ohouse.web.domain.product.review.ReviewPageDTO;
import com.ohouse.web.domain.product.review.ReviewSummaryDTO;
import com.ohouse.web.service.product.ReviewService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Controller
@Log4j
@RequestMapping("/")
@RequiredArgsConstructor
public class ReviewController {

	@Autowired
	private ReviewService reviewService;

	// 기존 JSP에서 쓰던 /review.htm 주소 그대로 매핑
	@GetMapping("/review.htm")
	public String getReviewList(
			@RequestParam(value = "product_id", defaultValue = "3377041") int productId,
			@RequestParam(value = "page", defaultValue = "1") int currentPage,
			@RequestParam(value = "sort", defaultValue = "best") String sort,
			@RequestParam(value = "ratings", required = false) List<Integer> ratings,
			@RequestParam(value = "options", required = false) List<Integer> options,
			HttpSession session, Model model, HttpServletRequest request) throws ClassNotFoundException, SQLException {

		if (productId == 1) {
			productId = 3377041;
		}

		// 사용자 인증 정보 세팅
		AuthUserDTO authUser = (AuthUserDTO) session.getAttribute("authUser");
		int memberId = 0;
		String role = "";
		//------------임시--------------

		authUser = (AuthUserDTO) session.getAttribute("authUser");
		if (authUser == null) {
			authUser = new AuthUserDTO(1, "admin", "관리자", "ADMIN");
			session.setAttribute("authUser", authUser);
		}
		memberId = authUser.getMemberId();

		//-----------------------------

		if(authUser != null) {
			memberId = authUser.getMemberId();
			role = authUser.getRole();
		}
		boolean isAdmin = "ADMIN".equals(role);

		int numberPerPage = 5;
		log.info("productId: " + productId + ", ratings: " + ratings + ", options: " + options);

		// DTO 조립 (ratings, options 필터 정상 빌드 추가 완료)
		ReviewPageDTO reqDTO = ReviewPageDTO.builder()
				.productId(productId)
				.currentPage(currentPage)
				.numberPerPage(numberPerPage)
				.sort(sort)
				.ratings(ratings)
				.options(options)
				.memberId(memberId)
				.build();

		// 서비스 호출
		List<ReviewDTO> reviewList = reviewService.getReviewList(reqDTO);
		ReviewSummaryDTO reviewSummary = reviewService.getReviewSummary(productId);
		int totalRecords = reviewService.getTotalRecords(reqDTO);
		List<OptionFilterDTO> optionFilterList = reviewService.getOptionFilterList(productId);
		PageDTO pageDTO = new PageDTO(totalRecords, currentPage, numberPerPage);

		// Model에 데이터 담기 (기존 핸들러와 동일하게 모든 속성 맞춤)
		model.addAttribute("reviewList", reviewList);
		model.addAttribute("reviewSummary", reviewSummary);
		model.addAttribute("pageDTO", pageDTO);
		model.addAttribute("currentSort", sort);
		model.addAttribute("product_id", productId);
		model.addAttribute("selectedRatings", ratings);
		model.addAttribute("selectedOptions", options);
		model.addAttribute("isAdmin", isAdmin);
		model.addAttribute("optionFilterList", optionFilterList);

		log.info("optionFilterList: " + optionFilterList);

		// 비동기(Fetch) 요청인지 확인 (X-Requested-With 헤더 체크)
		boolean isAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));

		if (isAjax) {
			// 비동기 요청이면 리뷰 아이템만 그려주는 조각 JSP로 리턴
			return "product/review/reviewItem";
		}

		// 일반 요청이면 전체 페이지 리턴
		return "product/review/reviewList";
	}
	// ==========================================
    // 2. 리뷰 작성 (WriteReviewHandler 대체)
    // ==========================================
    @PostMapping("/review/write.htm")
    public String writeReview(
            @RequestParam("productId") int productId,
            @RequestParam("rating") int rating,
            @RequestParam("content") String content,
            @RequestParam(value = "productOptionId", required = false) Integer productOptionId,
            @RequestParam(value = "isPurchased", defaultValue = "0") int isPurchased,
            @RequestParam(value = "reviewImage", required = false) MultipartFile reviewImage,
            HttpSession session) throws Exception {

        AuthUserDTO authUser = (AuthUserDTO) session.getAttribute("authUser");
        
        //------------임시--------------
        if (authUser == null) {
            authUser = new AuthUserDTO(1, "admin", "관리자", "ADMIN");
            session.setAttribute("authUser", authUser);
        }
        //-----------------------------

        int memberId = authUser.getMemberId();

        ReviewDTO reviewDTO = new ReviewDTO();
        reviewDTO.setProductId(productId);
        reviewDTO.setMemberId(memberId);
        reviewDTO.setRating(rating);
        reviewDTO.setContent(content);
        reviewDTO.setProductOptionId(productOptionId != null ? productOptionId : 0);
        reviewDTO.setIsPurchased(isPurchased); // DTO 타입에 맞춰 조정 (isPurchased 여부)

        // 서비스 호출 (리뷰 등록 + 이미지 업로드 처리)
        boolean success = reviewService.registerReview(reviewDTO, reviewImage);

        if (!success) {
            log.error("❌ 리뷰 등록 실패!");
        }

        return "redirect:/product/productDetail.htm?product_id=" + productId;
    }
// ==========================================
	// 3. 리뷰 좋아요 / 도움돼요 토글 (핸들러 방식 이식)
	// ==========================================
	@PostMapping(value = "/review/helpCountToggle.htm")
	@ResponseBody
	public void toggleHelpCount(
			@RequestParam("review_id") int reviewId,
			HttpSession session,
			HttpServletResponse response) throws Exception {

		log.info("🐇🐇 toggleHelpCount 진입 성공! reviewId = " + reviewId);

		AuthUserDTO authUser = (AuthUserDTO) session.getAttribute("authUser");

		//------------임시--------------

		authUser = (AuthUserDTO) session.getAttribute("authUser");
		if (authUser == null) {
			authUser = new AuthUserDTO(1, "admin", "관리자", "ADMIN");
			session.setAttribute("authUser", authUser);
		}

		//-----------------------------
		else if (authUser == null) {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "LOGIN_REQUIRED");
			return;
		}



		int memberId = authUser.getMemberId();

		// 서비스 실행
		Map<String, Object> resultMap = reviewService.toggleHelpCount(reviewId, memberId);

		boolean isLiked = (Boolean) resultMap.get("isLiked");
		int helpCount = (Integer) resultMap.get("helpCount");

		// 응답 설정 및 직접 출력 (핸들러 방식 그대로 재현)
		response.setContentType("application/json; charset=UTF-8");
		try (PrintWriter out = response.getWriter()) {
			// 자바스크립트가 liked와 isLiked 둘 다 대응할 수 있게 같이 넣어주거나 기존 방식 유지
			out.print(String.format("{\"reviewId\": %d, \"liked\": %b, \"isLiked\": %b, \"helpCount\": %d}", 
					reviewId, isLiked, isLiked, helpCount));
			out.flush();
		}
	}

	// ==========================================
	// 4. 리뷰 이미지 숨김 토글 (HideImageToggleHandler 이식)
	// ==========================================
	@PostMapping(value = "/review/hideImageToggle.htm")
	@ResponseBody
	public void toggleHideImage(
			HttpServletRequest request,
			HttpServletResponse response,
			HttpSession session) throws Exception {

		log.info("🐇🐇 toggleHideImage 진입 성공!");

		AuthUserDTO authUser = (AuthUserDTO) session.getAttribute("authUser");

		// ------------- 임시 테스트용 (필요시 주석 해제 또는 유지) -------------
		if (authUser == null) {
			authUser = new AuthUserDTO(1, "admin", "관리자", "ADMIN");
			session.setAttribute("authUser", authUser);
		}
		// ------------------------------------------------------------------

		// 권한 체크 (관리자만 가능)
		if (authUser == null || !"ADMIN".equals(authUser.getRole())) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setContentType("application/json; charset=UTF-8");
			try (PrintWriter out = response.getWriter()) {
				out.print("{\"success\": false, \"message\": \"권한이 없습니다.\"}");
				out.flush();
			}
			return;
		}

		try {
			// JSP 핸들러처럼 JS가 보낸 JSON Body를 BufferedReader로 읽어오기
			StringBuilder buffer = new StringBuilder();
			String line;
			try (java.io.BufferedReader reader = request.getReader()) {
				while ((line = reader.readLine()) != null) {
					buffer.append(line);
				}
			}

			// 
			com.google.gson.Gson gson = new com.google.gson.Gson();
			Map<String, Object> data = gson.fromJson(buffer.toString(), Map.class);

			int reviewId = ((Number) data.get("reviewId")).intValue();
			int isHideImage = ((Number) data.get("isHideImage")).intValue();

			// 서비스 호출
			boolean result = reviewService.updateHideImage(reviewId, isHideImage);

			// 기존 JSP 핸들러와 정확히 똑같은 {"success": true/false} 형태로 응답
			response.setContentType("application/json; charset=UTF-8");
			try (PrintWriter out = response.getWriter()) {
				out.print("{\"success\": " + result + "}");
				out.flush();
			}

		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.setContentType("application/json; charset=UTF-8");
			try (PrintWriter out = response.getWriter()) {
				out.print("{\"success\": false, \"message\": \"서버 오류가 발생했습니다.\"}");
				out.flush();
			}
		}
	}

	// ==========================================
	// 5. 리뷰 삭제 (DeleteReviewHandler 대체)
	// ==========================================
	@GetMapping("/review/delete.htm")
	public String deleteReview(
			@RequestParam("reviewId") int reviewId,
			HttpSession session, HttpServletRequest request) throws Exception {

		AuthUserDTO authUser = (AuthUserDTO) session.getAttribute("authUser");
		//------------임시--------------

		authUser = (AuthUserDTO) session.getAttribute("authUser");
		if (authUser == null) {
			authUser = new AuthUserDTO(1, "admin", "관리자", "ADMIN");
			session.setAttribute("authUser", authUser);
		}
		int memberId = authUser.getMemberId();

		//-----------------------------
		if (authUser == null || !"ADMIN".equals(authUser.getRole())) {
			return "redirect:/login.htm";
		}

		boolean success = reviewService.removeReview(reviewId);

		String referer = request.getHeader("referer");
		return "redirect:" + (referer != null ? referer : "/index.htm");
	}

	// ==========================================
	// 6. 리뷰 수정 (EditReviewHandler 대체 - 간소화 버전)
	// ==========================================
	@PostMapping("/review/edit.htm")
	public String editReview(
			@RequestParam("reviewId") int reviewId,
			@RequestParam("productId") int productId,
			@RequestParam("rating") int rating,
			@RequestParam("content") String content,
			@RequestParam(value = "reviewImage", required = false) MultipartFile reviewImage,
			HttpSession session, HttpServletRequest request) throws Exception {

		AuthUserDTO authUser = (AuthUserDTO) session.getAttribute("authUser");
		//------------임시--------------

		authUser = (AuthUserDTO) session.getAttribute("authUser");
		if (authUser == null) {
			authUser = new AuthUserDTO(1, "admin", "관리자", "ADMIN");
			session.setAttribute("authUser", authUser);
		}
		int memberId = authUser.getMemberId();

		//-----------------------------
		if (authUser == null) {
			return "redirect:/login.htm";
		}

		String imageUrl = null;

		// 1. 이미지가 새로 업로드된 경우에만 R2 처리 수행
		if (reviewImage != null && !reviewImage.isEmpty()) {
			String originalFileName = reviewImage.getOriginalFilename();
			String savedFileName = UUID.randomUUID().toString() + "_" + originalFileName;
			String objectKey = "reviews/" + savedFileName;

			// R2 설정값 (기존 핸들러와 동일)
			String r2Endpoint = "https://c118a7efdddd35d3edac1db3a63ed76d.r2.cloudflarestorage.com";
			String r2AccessKey = "8f8a91958a3c06d4ce11ba80f5d60e2f";
			String r2SecretKey = "5ab97a22e5baa3fe630165a9e770f0eada870d8672aaea80d3258ccbc2440667";
			String r2Bucket = "productimage";
			String r2PublicUrl = "https://pub-3490b121289f419194b634a98c9d4ba5.r2.dev";

			S3Configuration s3Configuration = S3Configuration.builder().chunkedEncodingEnabled(false).build();

			try (S3Client s3Client = S3Client.builder()
					.endpointOverride(URI.create(r2Endpoint))
					.region(Region.of("auto"))
					.credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(r2AccessKey, r2SecretKey)))
					.serviceConfiguration(s3Configuration)
					.build()) {

				PutObjectRequest putObjectRequest = PutObjectRequest.builder()
						.bucket(r2Bucket)
						.key(objectKey)
						.contentType(reviewImage.getContentType())
						.build();

				// MultipartFile은 바로 bytes로 읽을 수 있어 훨씬 간편합니다.
				s3Client.putObject(putObjectRequest, software.amazon.awssdk.core.sync.RequestBody.fromBytes(reviewImage.getBytes()));

				imageUrl = r2PublicUrl + "/" + objectKey;
			}
		}

		// 2. DTO 세팅
		ReviewDTO reviewDTO = new ReviewDTO();
		reviewDTO.setReviewId(reviewId);
		reviewDTO.setMemberId(authUser.getMemberId());
		reviewDTO.setRating(rating);
		reviewDTO.setContent(content);

		// 3. 서비스 호출 (String 타입의 imageUrl 전달)
		boolean success = reviewService.modifyReview(reviewDTO, imageUrl);

		if (success) {
			return "redirect:/product/productDetail.htm?product_id=" + productId;
		} else {
			request.setAttribute("errorMessage", "리뷰 수정에 실패했습니다.");
			return "common/error";
		}
	}

	// ==========================================
	// 8. 리뷰 작성 가능 여부 체크 (ReviewCheckHandler 대체)
	// ==========================================
	@GetMapping("/review/check.htm")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> checkReviewable(
			@RequestParam(value = "product_id", defaultValue = "0") long productId,
			HttpSession session) throws ClassNotFoundException, SQLException {

		AuthUserDTO authUser = (AuthUserDTO) session.getAttribute("authUser");
		//------------임시--------------

		authUser = (AuthUserDTO) session.getAttribute("authUser");
		if (authUser == null) {
			authUser = new AuthUserDTO(1, "admin", "관리자", "ADMIN");
			session.setAttribute("authUser", authUser);
		}
		int memberId = authUser.getMemberId();

		//-----------------------------
		if (authUser == null) {
			return ResponseEntity.status(401).build();
		}

		boolean hasReviewed = reviewService.checkAndValidateUserReview(authUser.getMemberId(), productId);

		return ResponseEntity.ok(Map.of("hasReviewed", hasReviewed));
	}

	// ==========================================
	// 5. 관리자 답글 등록/수정 (AdminReplyHandler 이식)
	// ==========================================
	@PostMapping(value = "/review/adminReply.htm")
	@ResponseBody
	public void saveAdminReply(
			HttpServletRequest request,
			HttpServletResponse response,
			HttpSession session) throws Exception {

		log.info("🐇🐇 saveAdminReply 진입 성공!");

		AuthUserDTO authUser = (AuthUserDTO) session.getAttribute("authUser");

		// ------------- 임시 테스트용 -------------
		if (authUser == null) {
			authUser = new AuthUserDTO(1, "admin", "관리자", "ADMIN");
			session.setAttribute("authUser", authUser);
		}
		// -----------------------------------------

		response.setContentType("application/json; charset=UTF-8");

		try {
			if (authUser == null || !"ADMIN".equals(authUser.getRole())) {
				try (PrintWriter out = response.getWriter()) {
					out.print("{\"success\": false, \"message\": \"권한이 없습니다.\"}");
					out.flush();
				}
				return;
			}

			// 폼 파라미터 수신
			String reviewIdStr = request.getParameter("reviewId");
			String adminReply = request.getParameter("adminReply");

			if (reviewIdStr == null || reviewIdStr.isEmpty()) {
				try (PrintWriter out = response.getWriter()) {
					out.print("{\"success\": false, \"message\": \"리뷰 번호가 없습니다.\"}");
					out.flush();
				}
				return;
			}

			int reviewId = Integer.parseInt(reviewIdStr);
			boolean isAdmin = "ADMIN".equals(authUser.getRole());

			// 서비스 호출
			boolean success = reviewService.saveAdminReply(reviewId, adminReply, isAdmin);

			// 💡 PrintWriter를 확실하게 사용하고 flush 처리
			try (PrintWriter out = response.getWriter()) {
				if (success) {
					out.print("{\"success\": true}");
				} else {
					out.print("{\"success\": false, \"message\": \"DB 처리에 실패했습니다.\"}");
				}
				out.flush();
			}

		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			try (PrintWriter out = response.getWriter()) {
				out.print("{\"success\": false, \"message\": \"서버 오류: " + e.getMessage() + "\"}");
				out.flush();
			}
		}

	}
}