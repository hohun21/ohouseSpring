package com.ohouse.web.service.address;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ohouse.web.domain.address.ShippingAddressDTO;
import com.ohouse.web.mapper.address.AddressMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressMapper addressMapper;

    @Transactional(rollbackFor = Exception.class)
    public int addShippingAddress(ShippingAddressDTO dto) {
        int count = addressMapper.getAddressCount(dto.getMember_id());
        
        if (count >= 3) {
            return -1; 
        }
        
        if ("Y".equals(dto.getIs_default())) {
            addressMapper.resetDefaultAddress(dto.getMember_id());
        } 
        else if (count == 0) {
            dto.setIs_default("Y");
        }
        
        return addressMapper.insertAddress(dto);
    }

    public List<ShippingAddressDTO> getAddressList(int memberId) {
        return addressMapper.getAddressList(memberId);
    }
    
    @Transactional(rollbackFor = Exception.class)
    public boolean setDefaultAddress(int addressId, int memberId) {
        addressMapper.resetDefaultAddress(memberId);
        int result = addressMapper.updateDefaultAddress(addressId, memberId);
        
        return result > 0;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean deleteAddress(int addressId, int memberId) {
        int result = addressMapper.deleteAddress(addressId, memberId);
        return result > 0;
    }
}