package com.ohouse.web.service.auth;

import java.util.Map;

import com.ohouse.web.mapper.auth.SellerAuthMapper;
import com.ohouse.web.domain.auth.SellerSignupRequest;
import com.ohouse.web.domain.seller.SellerDTO;
import com.ohouse.web.security.LegacyPasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SellerAuthService {
    private final SellerAuthMapper mapper;
    private final PasswordEncoder encoder;

    @Transactional
    public void signup(SellerSignupRequest req, Map<String, Boolean> errors) {
       
    	if (mapper.countEmail(req.getEmail()) > 0) errors.put("duplicateEmail", true);
        if (mapper.countBrand(req.getBrandName().trim()) > 0) errors.put("duplicateBrand", true);
        if (mapper.countBusiness(req.getBusinessNumber()) > 0) errors.put("duplicateBusiness", true);
        if (mapper.countMailOrder(req.getMailOrderNumber().trim()) > 0) errors.put("duplicateMailOrder", true);
        if (!errors.isEmpty()) return;
       
        SellerDTO seller = new SellerDTO();
        seller.setEmail(req.getEmail().toLowerCase());
        seller.setPassword(encoder.encode(req.getPassword()));
        seller.setBusinessNumber(req.getBusinessNumber());
        seller.setRepresentativeName(req.getRepresentativeName().trim());
        seller.setMailOrderNumber(req.getMailOrderNumber().trim());
        seller.setBusinessAddress(req.getBusinessAddress());
        seller.setRepresentativeContact(req.getRepresentativeContact().trim());
        seller.setCustomerServicePhone(req.getCustomerServicePhone().trim());
       
        if (mapper.insertSeller(seller) != 1 || mapper.insertBrand(seller.getSellerId(), req.getBrandName().trim()) != 1)
            throw new IllegalStateException("판매자 등록에 실패했습니다.");
        
    }

    @Transactional
    public SellerDTO authenticate(String email, String password, String businessNumber) {
    	SellerDTO seller = mapper.findByEmail(email);
       
    	if (seller == null || password == null || !encoder.matches(password, seller.getPassword()) ||
                !businessNumber.equals(digits(seller.getBusinessNumber())) || seller.getEmail() == null)
            throw new IllegalArgumentException("이메일, 비밀번호 또는 사업자등록번호가 일치하지 않습니다.");
        
    	if (!"ACTIVE".equalsIgnoreCase(seller.getStatus()))
            throw new IllegalStateException("승인된 판매자 계정만 로그인할 수 있습니다.");
        
    	upgradePassword(seller, password);
        return seller;
    }

    private void upgradePassword(SellerDTO seller, String raw) {
       
    	if (LegacyPasswordEncoder.isBcrypt(seller.getPassword())) return;
        String hash = encoder.encode(raw);
        
        if (mapper.upgradeLegacyPassword(seller.getSellerId(), seller.getPassword(), hash) != 1)
            throw new IllegalStateException("계정 정보가 변경되었습니다. 다시 로그인해주세요.");
        
        seller.setPassword(hash);
    }

    @Transactional
    public String signupStatus(String email, String password, String businessNumber) {
    	
    	SellerDTO seller = mapper.findByEmail(email);
        
    	if (seller == null || password == null || !encoder.matches(password, seller.getPassword()) ||
                !businessNumber.equals(digits(seller.getBusinessNumber())))
            throw new IllegalArgumentException("이메일, 비밀번호 또는 사업자등록번호가 일치하지 않습니다.");
        
    	upgradePassword(seller, password);
        return seller.getStatus();
    }

    public boolean checkPassword(int sellerId, String email, String current) {
    	SellerDTO seller = mapper.findByEmail(email);
      
        return seller != null && seller.getSellerId() == sellerId && current != null && encoder.matches(current, seller.getPassword());
    }
    
    @Transactional
    public void changePassword(int sellerId, String email, String current, String next) {
        if (!checkPassword(sellerId, email, current)) throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        if (mapper.updatePassword(sellerId, encoder.encode(next)) != 1) throw new IllegalStateException("비밀번호 변경에 실패했습니다.");
    }
  
    public int count(String field, String value) {
        switch (field) {
            case "email": return mapper.countEmail(value);
            case "brandName": return mapper.countBrand(value);
            case "businessNumber": return mapper.countBusiness(value);
            case "mailOrderNumber": return mapper.countMailOrder(value);
            default: throw new IllegalArgumentException("허용되지 않는 검사 항목입니다.");
        }
    }
   
    public String status(String email) { SellerDTO s = mapper.findByEmail(email); return s == null ? null : s.getStatus(); }
   
    public static String digits(String value) { return value == null ? "" : value.replaceAll("[^0-9]", ""); }
}
