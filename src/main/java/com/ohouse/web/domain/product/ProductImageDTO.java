package com.ohouse.web.domain.product;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductImageDTO {

    private String image_url;
    private int sort_order;
}
