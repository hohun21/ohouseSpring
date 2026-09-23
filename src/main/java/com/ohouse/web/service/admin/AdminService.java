package com.ohouse.web.service.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ohouse.web.domain.member.MemberDTO;
import com.ohouse.web.domain.seller.ProductDTO;
import com.ohouse.web.domain.seller.SellerDTO;
import com.ohouse.web.mapper.admin.AdminMapper;
import com.ohouse.web.mapper.seller.SellerMapper;

@Service
public class AdminService {

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private SellerMapper sellerMapper;
    
    public List<SellerDTO> getPendingSellers() {
        return sellerMapper.getPendingSellers();
    }

    @Transactional
    public boolean updateSellerStatus(int sellerId, String status) {
        int result = sellerMapper.updateSellerStatus(sellerId, status);
        return result > 0;
    }

    public int getPendingSellerCount() {
        return sellerMapper.getPendingSellerCount();
    }

    public List<SellerDTO> getPendingSellersWithPaging(int startRow, int endRow) {
        return sellerMapper.getPendingSellersWithPaging(startRow, endRow);
    }
    
    public int getTotalSellerCount() {
        return sellerMapper.getTotalSellerCount();
    }

    public List<SellerDTO> getSellerListWithPaging(int startRow, int endRow) {
        return sellerMapper.getSellerListWithPaging(startRow, endRow);
    }

    @Transactional
    public boolean deleteSeller(int sellerId) {
        int result = sellerMapper.deleteSeller(sellerId);
        return result > 0;
    }
    
    public List<MemberDTO> getAllMembers() {
        return adminMapper.getAllMembers();
    }

    public int getTotalMemberCount() {
        return adminMapper.getTotalMemberCount();
    }

    public List<MemberDTO> getMemberListWithPaging(int startRow, int endRow) {
        return adminMapper.getMemberListWithPaging(startRow, endRow);
    }

    @Transactional
    public boolean changeMemberStatus(int memberId, int status) {
        int result = adminMapper.updateMemberStatus(memberId, status);
        return result > 0;
    }
    
    public List<ProductDTO> getAllProductsForAdmin() {
        return sellerMapper.getAllProductsForAdmin();
    }

    @Transactional
    public boolean deleteProductByAdmin(int productId) {
        int result = sellerMapper.deleteProduct(productId);
        return result > 0;
    }
}