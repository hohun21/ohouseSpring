package com.ohouse.web.mapper.product;

import java.sql.SQLException;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ohouse.web.domain.product.review.OptionFilterDTO;
import com.ohouse.web.domain.product.review.ReviewDTO;
import com.ohouse.web.domain.product.review.ReviewPageDTO;
import com.ohouse.web.domain.product.review.ReviewSummaryDTO;

public interface ReviewMapper {
	
	// 1. 특정 상품의 리뷰 통계 (평균 별점, 개수)
		ReviewSummaryDTO selectReviewSummary(long productId) throws ClassNotFoundException, SQLException ;

		int getTotalRecords(ReviewPageDTO reqDTO) throws ClassNotFoundException, SQLException ;

		List<OptionFilterDTO> selectOptionFilterList(long productId) throws ClassNotFoundException, SQLException ;

		boolean isReviewLiked(@Param("reviewId") int reviewId, @Param("memberId") int memberId) throws ClassNotFoundException, SQLException ;

		int insertReviewLike(@Param("reviewId") int reviewId, @Param("memberId") int memberId) throws ClassNotFoundException, SQLException ;

		int deleteReviewLike(@Param("reviewId") int reviewId, @Param("memberId") int memberId) throws ClassNotFoundException, SQLException ;

		int getHelpCount(int reviewId) throws ClassNotFoundException, SQLException ;

		List<ReviewDTO> selectReviewList(ReviewPageDTO reqDTO) throws ClassNotFoundException, SQLException ;

		int updateHideImage(@Param("reviewId") int reviewId, @Param("isHideImage") int isHideImage) throws ClassNotFoundException, SQLException ;
		
		int updateAdminReply(@Param("reviewId") int reviewId, @Param("adminReply") String adminReply) throws ClassNotFoundException, SQLException ;

		int insertReviewImage(@Param("reviewId") int reviewId, @Param("imageUrl") String imageUrl) throws ClassNotFoundException, SQLException ;

		int insertReview(ReviewDTO reviewDTO) throws ClassNotFoundException, SQLException ;

		int updateReview(ReviewDTO reviewDTO) throws ClassNotFoundException, SQLException ;

		int updateReviewImage(@Param("reviewId") int reviewId, @Param("imageUrl") String imageUrl) throws ClassNotFoundException, SQLException ;

		int deleteReviewLikes(int reviewId) throws ClassNotFoundException, SQLException ;

		int deleteReviewImages(int reviewId) throws ClassNotFoundException, SQLException ;

		int deleteReview(int reviewId) throws ClassNotFoundException, SQLException ;

		int selectMyReviewTotalCount(int memberId) throws ClassNotFoundException, SQLException ;

		List<ReviewDTO> selectMyReviewList(ReviewPageDTO reqDTO) throws ClassNotFoundException, SQLException ;

		boolean hasUserPurchased(@Param("memberId") int memberId, @Param("productId") long productId) throws ClassNotFoundException, SQLException ;

		//int insertReview2(ReviewDTO reviewDTO); // backup

		boolean hasUserReviewedProduct(@Param("memberId") long memberId, @Param("productId") long productId) throws ClassNotFoundException, SQLException ;

		ReviewDTO findLatestOrderInfo(@Param("memberId") int memberId, @Param("productId") long productId) throws ClassNotFoundException, SQLException ;

//	AdminReplyHandler
//	DeleteReviewHandler
//	EditReviewHandler
//	HelpCountToggleHandler
//	HideImageToggleHandler
//	ReviewCheckHandler
//	ReviewListHandler
//	WriteReviewHandler.
	
}	
	
	
	

