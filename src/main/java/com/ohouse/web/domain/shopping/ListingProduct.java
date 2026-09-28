package com.ohouse.web.domain.shopping;

import java.util.Date;
import lombok.Data;

@Data
public class ListingProduct {
    private int rank;
    private int productId;
    private String brandName;
    private String productName;
    private double discountRate;
    private long price;
    private double reviewScore;
    private int reviewCount;
    private String imageUrl;
    private int salesQty;
    private Date created;
}
