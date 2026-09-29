package com.ohouse.web.service.product;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ohouse.web.domain.product.review.OptionFilterDTO;
import com.ohouse.web.domain.product.review.ReviewDTO;
import com.ohouse.web.domain.product.review.ReviewPageDTO;
import com.ohouse.web.domain.product.review.ReviewSummaryDTO;

@Service
public interface ReviewService {

    List<ReviewDTO> getReviewList(ReviewPageDTO reqDTO) throws ClassNotFoundException, SQLException;

    ReviewSummaryDTO getReviewSummary(long productId)throws ClassNotFoundException, SQLException;

    int getTotalRecords(ReviewPageDTO reqDTO)throws ClassNotFoundException, SQLException;

    List<OptionFilterDTO> getOptionFilterList(long productId)throws ClassNotFoundException, SQLException;
    
    // 5. 리뷰 등록 (이미지 포함)
    boolean registerReview(ReviewDTO reviewDTO, MultipartFile reviewImage) throws ClassNotFoundException, SQLException, Exception;
    // 6. 리뷰 수정 (이미지 포함)
    boolean modifyReview(ReviewDTO reviewDTO, MultipartFile reviewImage) throws ClassNotFoundException, SQLException;

    // 7. 리뷰 삭제 (관련 좋아요/이미지 데이터 함께 정리)
    boolean removeReview(int reviewId) throws ClassNotFoundException, SQLException;

    // 8. 도움돼요(좋아요) 토글 처리 (좋아요 상태 및 갱신된 카운트 리턴)
    Map<String, Object> toggleHelpCount(int reviewId, int memberId) throws ClassNotFoundException, SQLException;

    // 9. 관리자 답글 등록/수정
    boolean saveAdminReply(int reviewId, String adminReply, boolean isAdmin) throws ClassNotFoundException, SQLException;

    // 10. 리뷰 이미지 숨김 처리 (관리자용)
    boolean updateHideImage(int reviewId, int isHideImage) throws ClassNotFoundException, SQLException;

    // 11. 사용자의 리뷰 작성 가능 여부 및 기존 리뷰 작성 여부 체크
    boolean checkAndValidateUserReview(int memberId, long productId) throws ClassNotFoundException, SQLException;

    int selectMyReviewTotalCount(int memberId) throws ClassNotFoundException, SQLException ;

	List<ReviewDTO> selectMyReviewList(ReviewPageDTO reqDTO) throws ClassNotFoundException, SQLException ;

}
