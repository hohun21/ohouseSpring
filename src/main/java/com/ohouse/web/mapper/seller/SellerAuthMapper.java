package com.ohouse.web.mapper.seller;

import org.apache.ibatis.annotations.Param;
import com.ohouse.web.domain.seller.SellerVO;

public interface SellerAuthMapper {
    SellerVO findByEmail(@Param("email") String email);
    int countEmail(@Param("email") String email);
    int countBrand(@Param("brandName") String brandName);
    int countBusiness(@Param("businessNumber") String businessNumber);
    int countMailOrder(@Param("mailOrderNumber") String mailOrderNumber);
    int insertSeller(SellerVO seller);
    int insertBrand(@Param("sellerId") int sellerId, @Param("brandName") String brandName);
    int upgradeLegacyPassword(@Param("sellerId") int sellerId, @Param("oldPassword") String oldPassword, @Param("password") String password);
    int updatePassword(@Param("sellerId") int sellerId, @Param("password") String password);
}
