package com.ohouse.web.domain.product;

import lombok.*;

@Getter
@Setter

@NoArgsConstructor
public class ProductDTO {

    private long brand_id;
    private String brand_name;
    private String product_name;
    private long product_id;
    private long original_price;
    private String image_url;
    private long price;
    private long category_id;
    private double discount_rate;

    private double avgRating;
    private int reviewCount;

    public ProductDTO(
            long brand_id,
            String product_name,
            long original_price,
            long price,
            long category_id,
            double discount_rate
    ) {
        this.brand_id = brand_id;
        this.product_name = product_name;
        this.original_price = original_price;
        this.price = price;
        this.category_id = category_id;
        this.discount_rate = discount_rate;
    }

}
