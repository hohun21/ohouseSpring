package com.ohouse.web.domain.seller;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SellerAuthDTO implements Serializable{
	private static final long serialVersionUID = 1L;
    private int sellerId;
    private String email;
    private String businessNumber;
    private String brandName;
    private String status;
}