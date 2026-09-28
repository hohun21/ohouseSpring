package com.ohouse.web.domain.seller;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductOptionDTO {
    private Integer productOptionId;
    private Integer productId;
    private String sku;
    private Integer price;
    private Integer stock;
    private String status;
}
