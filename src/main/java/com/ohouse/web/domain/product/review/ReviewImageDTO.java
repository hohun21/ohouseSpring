package com.ohouse.web.domain.product.review;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewImageDTO {
    private int imgId;               // IMG_ID (PK)
    private int reviewId;            // REVIEW_ID (FK)
    private String imageUrl; // 외부 웹 URL 전체 경로
}