package com.ohouse.web.service.seller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ohouse.web.domain.seller.SellerOrderDTO;
import com.ohouse.web.mapper.seller.SellerOrderMapper;

@Service
public class SellerOrderService {

    @Autowired
    private SellerOrderMapper sellerOrderMapper;

    public List<SellerOrderDTO> getOrderList(String brandName) {
        return sellerOrderMapper.selectOrderListByBrand(brandName);
    }

    @Transactional
    public boolean changeDeliveryStatus(int orderDetailId, int status) {
        int result = sellerOrderMapper.updateDeliveryStatus(orderDetailId, status);
        return result > 0;
    }

    public Map<String, Object> getDashboardOrderStats(String brandName) {
        return sellerOrderMapper.selectDashboardOrderStats(brandName);
    }
    
    public List<SellerOrderDTO> getClaimList(String brandName) {
        return sellerOrderMapper.selectClaimListByBrand(brandName);
    }

    public List<SellerOrderDTO> getSettlementList(String brandName) {
        return sellerOrderMapper.selectSettlementListByBrand(brandName);
    }
}