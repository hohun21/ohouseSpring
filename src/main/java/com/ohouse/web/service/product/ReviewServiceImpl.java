package com.ohouse.web.service.product;

import java.io.File;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ohouse.web.domain.product.review.OptionFilterDTO;
import com.ohouse.web.domain.product.review.ReviewDTO;
import com.ohouse.web.domain.product.review.ReviewPageDTO;
import com.ohouse.web.domain.product.review.ReviewSummaryDTO;
import com.ohouse.web.mapper.product.ReviewMapper;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewMapper reviewMapper;

    @Override
    public List<ReviewDTO> getReviewList(ReviewPageDTO reqDTO) throws ClassNotFoundException, SQLException {
        return reviewMapper.selectReviewList(reqDTO);
    }

    @Override
    public ReviewSummaryDTO getReviewSummary(long productId) throws ClassNotFoundException, SQLException {
        return reviewMapper.selectReviewSummary(productId);
    }

    @Override
    public int getTotalRecords(ReviewPageDTO reqDTO) throws ClassNotFoundException, SQLException {
        return reviewMapper.getTotalRecords(reqDTO);
    }

    @Override
    public List<OptionFilterDTO> getOptionFilterList(long productId) throws ClassNotFoundException, SQLException {
        return reviewMapper.selectOptionFilterList(productId);
    }

    @Override
    public boolean registerReview(ReviewDTO reviewDTO, MultipartFile reviewImage) throws Exception {
        
        // 1. 기존 JSP 코드의 의도대로 기본값 세팅 (이력이 없어도 기본 필드는 채워지도록)
        ReviewDTO orderInfo = reviewMapper.findLatestOrderInfo(reviewDTO.getMemberId(), reviewDTO.getProductId());
        
        Integer resolvedOptionId = null;
        int isPurchased = 0;
        
        if (orderInfo != null) {
            resolvedOptionId = orderInfo.getProductOptionId();
            isPurchased = 1; // 주문 내역이 있으므로 구매자 확정 (1)
        }
        
        // 2. 사용자가 폼에서 직접 선택한 옵션 ID가 있다면 우선 적용, 없으면 최신 주문 옵션 ID 사용
        Integer finalOptionId = (reviewDTO.getProductOptionId() != null && reviewDTO.getProductOptionId() > 0) 
                                ? reviewDTO.getProductOptionId() 
                                : resolvedOptionId;
        
        // 3. 최종 reviewDTO에 값 세팅
        reviewDTO.setProductOptionId(finalOptionId);
        reviewDTO.setIsPurchased(isPurchased); // int형 (0 또는 1)

        // 4. 리뷰 본문 INSERT 실행 (<selectKey> 덕분에 reviewId가 reviewDTO에 자동 주입됨)
        int result = reviewMapper.insertReview(reviewDTO);
        if (result <= 0) {
            return false;
        }

        // 5. 첨부된 이미지가 있는 경우 파일 저장 후 이미지 인서트
        if (reviewImage != null && !reviewImage.isEmpty()) {
            String uploadDir = "C:/ohouse/upload/review/"; 
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String originalFilename = reviewImage.getOriginalFilename();
            String savedFileName = UUID.randomUUID().toString() + "_" + originalFilename;
            
            File target = new File(uploadDir, savedFileName);
            reviewImage.transferTo(target);

            String imageUrl = "/upload/review/" + savedFileName;

            // 이미지 매퍼 호출
            reviewMapper.insertReviewImage(reviewDTO.getReviewId(), imageUrl);
        }

        return true;
    }

	@Override
	public boolean modifyReview(ReviewDTO reviewDTO, String imageUrl) throws ClassNotFoundException, SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeReview(int reviewId) throws ClassNotFoundException, SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Map<String, Object> toggleHelpCount(int reviewId, int memberId) throws ClassNotFoundException, SQLException {
		// 1. 이미 좋아요를 눌렀는지 확인 (Mapper에 정의된 isReviewLiked 호출)
				int likedCount = reviewMapper.isReviewLiked(reviewId, memberId);
				boolean isLiked = (likedCount > 0);
				if (isLiked) {
					// 2. 이미 눌렀다면 취소 (DELETE)
					reviewMapper.deleteReviewLike(reviewId, memberId);
					isLiked = false;
				} else {
					// 3. 안 눌렀다면 추가 (INSERT)
					reviewMapper.insertReviewLike(reviewId, memberId);
					isLiked = true;
				}
				
				// 4. 최신 좋아요 수 조회 (Mapper에 정의된 getHelpCount 호출)
				int helpCount = reviewMapper.getHelpCount(reviewId);
				
				// 5. 결과를 Map에 담아서 컨트롤러로 반환
				Map<String, Object> resultMap = new java.util.HashMap<>();
				resultMap.put("isLiked", isLiked);
				resultMap.put("helpCount", helpCount);
				
				return resultMap;
	}

	@Override
	public boolean saveAdminReply(int reviewId, String adminReply, boolean isAdmin)
			throws ClassNotFoundException, SQLException {
		int result = reviewMapper.saveAdminReply(reviewId, adminReply, isAdmin);
		return result > 0;
	}

	@Override
	public boolean updateHideImage(int reviewId, int isHideImage) throws ClassNotFoundException, SQLException {
		int result = reviewMapper.updateHideImage(reviewId, isHideImage);
	    return result > 0;
	}

	@Override
	public boolean checkAndValidateUserReview(int memberId, long productId)
			throws ClassNotFoundException, SQLException {
		// TODO Auto-generated method stub
		return false;
	}
}