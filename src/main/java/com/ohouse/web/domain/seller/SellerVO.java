package com.ohouse.web.domain.seller;

import lombok.Data;

@Data
public class SellerVO {
    private int sellerId;
    private String email;
    private String password;
    private String businessNumber;
    private String representativeName;
    private String mailOrderNumber;
    private String businessAddress;
    private String representativeContact;
    private String customerServicePhone;
    private String status;
    private String brandName;
}
