package com.ohouse.web.service.product;

import java.io.File;
import java.net.URI;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.ohouse.web.domain.product.review.OptionFilterDTO;
import com.ohouse.web.domain.product.review.ReviewDTO;
import com.ohouse.web.domain.product.review.ReviewPageDTO;
import com.ohouse.web.domain.product.review.ReviewSummaryDTO;
import com.ohouse.web.domain.product.review.SubOptionDTO;
import com.ohouse.web.mapper.product.ReviewMapper;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class ReviewServiceImpl implements ReviewService {
    //r2 사용
    // 👉 ProductAddProHandler에서 사용중인 공용 R2 설정값 그대로 연동
    private static final String R2_ENDPOINT = "https://c118a7efdddd35d3edac1db3a63ed76d.r2.cloudflarestorage.com";
    private static final String R2_ACCESS_KEY = "8f8a91958a3c06d4ce11ba80f5d60e2f";
    private static final String R2_SECRET_KEY = "5ab97a22e5baa3fe630165a9e770f0eada870d8672aaea80d3258ccbc2440667";
    private static final String R2_BUCKET = "productimage";
    private static final String R2_PUBLIC_URL = "https://pub-3490b121289f419194b634a98c9d4ba5.r2.dev";

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
        // 1. 필수 옵션 그룹 개수 확인
        int groupCount = reviewMapper.countRequiredOptionGroup(productId);

        // 2. 그룹 개수에 따라 단일/복수 쿼리 분기 호출
        List<Map<String, Object>> rawList;
        if (groupCount <= 1) {
            rawList = reviewMapper.selectSingleOptionFilterList(productId);
        } else {
            rawList = reviewMapper.selectMultiOptionFilterList(productId);
        }

        // 3. 자바 메모리에서 DTO 계층 구조로 조립 (JSP 때 쓰셨던 루프 로직 그대로!)
        Map<Integer, OptionFilterDTO> map = new LinkedHashMap<>();

        for (Map<String, Object> row : rawList) {
            int parentValId = ((Number) row.get("PARENT_VAL_ID")).intValue();
            String parentValName = (String) row.get("PARENT_VAL_NAME");
            int productOptionId = ((Number) row.get("PRODUCT_OPTION_ID")).intValue();
            String subOptionName = (String) row.get("SUB_OPTION_NAME");

            OptionFilterDTO parentDTO = map.computeIfAbsent(parentValId, k -> 
                OptionFilterDTO.builder()
                    .optionValueId(parentValId)
                    .optionValueName(parentValName)
                    .subOptions(new ArrayList<>())
                    .build()
            );

            // subOptionName 중복 체크
            boolean exists = parentDTO.getSubOptions().stream()
                    .anyMatch(sub -> sub.getSubOptionName().equals(subOptionName));

            if (!exists) {
                SubOptionDTO subDTO = SubOptionDTO.builder()
                        .productOptionId(productOptionId)
                        .subOptionName(subOptionName)
                        .build();

                parentDTO.getSubOptions().add(subDTO);
            }
        }

        System.out.println(">>> 상품 ID: " + productId + " 의 옵션 그룹 개수(groupCount): " + groupCount);
        return new ArrayList<>(map.values());
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

     // 2. 첨부된 이미지가 있는 경우 R2 업로드 후 이미지 인서트
        if (reviewImage != null && !reviewImage.isEmpty()) {
            try {
                String originalFilename = reviewImage.getOriginalFilename();
                
                if (originalFilename != null && !originalFilename.isEmpty()) {
                    // 수업 때 배운 UUID 적용 방식 유지
                    String savedFileName = UUID.randomUUID().toString() + "_" + originalFilename;
                    String objectKey = "reviews/" + savedFileName; // R2 내부에 저장될 폴더 경로 지정

                    // 스트림 재사용 오류 방지를 위해 바이트 배열로 미리 읽기
                    byte[] fileBytes;
                    try (java.io.InputStream is = reviewImage.getInputStream()) {
                        fileBytes = is.readAllBytes();
                    }

                    // S3/R2 클라이언트 설정
                    S3Configuration s3Configuration = S3Configuration.builder()
                            .chunkedEncodingEnabled(false)
                            .build();

                    try (S3Client s3Client = S3Client.builder()
                            .endpointOverride(URI.create(R2_ENDPOINT))
                            .region(Region.of("auto"))
                            .credentialsProvider(
                                    StaticCredentialsProvider.create(
                                            AwsBasicCredentials.create(R2_ACCESS_KEY, R2_SECRET_KEY)
                                    )
                            )
                            .serviceConfiguration(s3Configuration)
                            .build()) {

                        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                                .bucket(R2_BUCKET)
                                .key(objectKey)
                                .contentType(reviewImage.getContentType())
                                .build();

                        // R2로 파일 업로드 실행
                        s3Client.putObject(
                                putObjectRequest,
                                RequestBody.fromBytes(fileBytes)
                        );

                        // R2 퍼블릭 URL 조합 (DB에 저장할 최종 경로)
                        String imageUrl = R2_PUBLIC_URL + "/" + objectKey;

                        // 이미지 매퍼 호출 (방금 생성된 reviewDTO.getReviewId() 활용)
                        reviewMapper.insertReviewImage(reviewDTO.getReviewId(), imageUrl);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("R2 파일 업로드 중 오류 발생: " + e.getMessage());
            }
        }

        return true;
    }

    @Transactional
    @Override
    public boolean modifyReview(ReviewDTO reviewDTO, MultipartFile reviewImage) throws ClassNotFoundException, SQLException {
        
        // 1. 리뷰 기본 정보 수정 (텍스트, 별점 등)
        int resultReview = this.reviewMapper.updateReview(reviewDTO);
        if (resultReview <= 0) {
            return false;
        }

        // 2. 이미지가 새로 업로드된 경우에만 R2 처리 수행
        if (reviewImage != null && !reviewImage.isEmpty()) {
            try {
                String originalFileName = reviewImage.getOriginalFilename();
                if (originalFileName != null && !originalFileName.isEmpty()) {
                    String savedFileName = UUID.randomUUID().toString() + "_" + originalFileName;
                    String objectKey = "reviews/" + savedFileName;

                    S3Configuration s3Configuration = S3Configuration.builder()
                            .chunkedEncodingEnabled(false)
                            .build();

                    try (S3Client s3Client = S3Client.builder()
                            .endpointOverride(URI.create(R2_ENDPOINT))
                            .region(Region.of("auto"))
                            .credentialsProvider(StaticCredentialsProvider.create(
                                    AwsBasicCredentials.create(R2_ACCESS_KEY, R2_SECRET_KEY)))
                            .serviceConfiguration(s3Configuration)
                            .build()) {

                        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                                .bucket(R2_BUCKET)
                                .key(objectKey)
                                .contentType(reviewImage.getContentType())
                                .build();

                        // R2로 파일 업로드 실행
                        s3Client.putObject(putObjectRequest, 
                                software.amazon.awssdk.core.sync.RequestBody.fromBytes(reviewImage.getBytes()));

                        // 퍼블릭 URL 조합
                        String imageUrl = R2_PUBLIC_URL + "/" + objectKey;

                        // 3. 이미지 테이블 수정/업데이트 매퍼 호출 
                        // (주의: resultReview는 업데이트된 행 수이므로, 리뷰 ID인 reviewDTO.getReviewId()를 전달해야 합니다!)
                        this.reviewMapper.updateReviewImage(reviewDTO.getReviewId(), imageUrl);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("리뷰 이미지 수정 중 R2 오류 발생: " + e.getMessage());
            }
        }

        return true;
    }

    
    
	@Override
	@Transactional
	public boolean removeReview(int reviewId) throws ClassNotFoundException, SQLException {
		try {
			//리뷰 아이디를 외래키로 가진 리뷰이미지, 도움돼요부터 처리
	        reviewMapper.deleteReviewImages(reviewId);
	        reviewMapper.deleteReviewLikes(reviewId);
	        
	        //리뷰  삭제
	        int deleteResult = reviewMapper.deleteReview(reviewId);
	        
	        // 4. 실제로 삭제된 리뷰가 존재하면 true 반환
	        return deleteResult > 0;
	        
	    } catch (Exception e) {
	        e.printStackTrace();
	        // 5. 에러를 던져야 @Transactional이 인식하고 롤백을 수행합니다!
	        throw new RuntimeException("리뷰 삭제 실패: " + e.getMessage());
	    }
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
		return reviewMapper.hasUserReviewedProduct(memberId, productId)> 0;
	}
}