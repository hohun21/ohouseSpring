package com.ohouse.web.domain.admin;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponDTO {
    private Integer couponId;
    private String couponName;
    private String discountType;
    private Integer discountValue;
    private Integer minOrderPrice;
    private Integer maxDiscount;
    private Date startDate;
    private Date endDate;
    private Integer status;
}