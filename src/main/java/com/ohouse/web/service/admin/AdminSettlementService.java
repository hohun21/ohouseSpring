package com.ohouse.web.service.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ohouse.web.domain.seller.SellerOrderDTO;
import com.ohouse.web.mapper.admin.AdminSettlementMapper;

@Service
public class AdminSettlementService {

    @Autowired
    private AdminSettlementMapper settlementMapper;

    public List<SellerOrderDTO> getAdminSettlementList() {
        return settlementMapper.selectAdminSettlementList();
    }
    
    @Transactional
    public boolean executeSettlement(int orderDetailId) {
        int result = settlementMapper.updateSettlementStatus(orderDetailId);
        return result > 0;
    }
}