package com.ohouse.web.mapper.seller;

import org.apache.ibatis.annotations.Param;
import com.ohouse.web.domain.seller.SellerDTO;

public interface SellerAuthMapper {
    
    int insert(SellerDTO seller);

    SellerDTO selectByEmail(@Param("email") String email);
    
    String emailCheck(@Param("email") String email);
    
    String businessNumberCheck(@Param("businessNumber") String businessNumber);
    
    String mailOrderNumberCheck(@Param("mailOrderNumber") String mailOrderNumber);

    String statusCheck(@Param("email") String email);

    int updatePassword(@Param("sellerId") long sellerId, @Param("newPassword") String newPassword);
}