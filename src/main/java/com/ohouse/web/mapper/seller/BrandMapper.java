package com.ohouse.web.mapper.seller;

import org.apache.ibatis.annotations.Param;

import com.ohouse.web.domain.seller.BrandDTO;

public interface BrandMapper {
    
    int insert(BrandDTO brand);

    BrandDTO selectByBrandName(@Param("brandName") String brandName);
    
    BrandDTO selectBySellerId(@Param("sellerId") int sellerId);
    
    String brandNameCheck(@Param("brandName") String brandName);
}