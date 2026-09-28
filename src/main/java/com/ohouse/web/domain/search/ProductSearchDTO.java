package com.ohouse.web.domain.search;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSearchDTO {
    private Integer productId;
    private String brandName;
    private String productName;
    private Integer price;
    private Integer discountRate;
    private String imageUrl;
    private String status;
    private Double avgRating;
    private Integer reviewCount;
}