package com.ohouse.web.mapper.auth;

import org.apache.ibatis.annotations.Param;
import com.ohouse.web.domain.seller.SellerDTO;

public interface SellerAuthMapper {
   
	SellerDTO findByEmail(@Param("email") String email);
    
	int countEmail(@Param("email") String email);

	int countBrand(@Param("brandName") String brandName);
    
	int countBusiness(@Param("businessNumber") String businessNumber);
    
	int countMailOrder(@Param("mailOrderNumber") String mailOrderNumber);
    
	int insertSeller(SellerDTO seller);

	int insertBrand(@Param("sellerId") int sellerId, @Param("brandName") String brandName);
    
	int upgradeLegacyPassword(@Param("sellerId") int sellerId, @Param("oldPassword") String oldPassword, @Param("password") String password);
    
	int updatePassword(@Param("sellerId") int sellerId, @Param("password") String password);
}