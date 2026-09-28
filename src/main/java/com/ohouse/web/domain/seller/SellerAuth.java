package com.ohouse.web.domain.seller;

import java.io.Serializable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SellerAuth implements Serializable {
    private static final long serialVersionUID = 1L;
    private final int sellerId;
    private final String email;
    private final String businessNumber;
    private final String brandName;
    private final String status;
}
