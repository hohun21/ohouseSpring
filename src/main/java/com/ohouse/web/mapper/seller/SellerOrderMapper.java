package com.ohouse.web.mapper.seller;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import com.ohouse.web.domain.seller.SellerOrderDTO;

public interface SellerOrderMapper {
    
    List<SellerOrderDTO> selectOrderListByBrand(@Param("brandName") String brandName);
    
    int updateDeliveryStatus(@Param("orderDetailId") int orderDetailId, @Param("status") int status);
    
    Map<String, Object> selectDashboardOrderStats(@Param("brandName") String brandName);
    
    List<SellerOrderDTO> selectClaimListByBrand(@Param("brandName") String brandName);
    
    List<SellerOrderDTO> selectSettlementListByBrand(@Param("brandName") String brandName);
}