package com.ohouse.web.mapper.admin;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ohouse.web.domain.seller.SellerOrderDTO;

public interface AdminSettlementMapper {
    
    List<SellerOrderDTO> selectAdminSettlementList();
    
    int updateSettlementStatus(@Param("orderDetailId") int orderDetailId);
}