package com.ohouse.web.mapper.address;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ohouse.web.domain.address.ShippingAddressDTO;

public interface AddressMapper {
    
    int getAddressCount(int memberId);

    int resetDefaultAddress(int memberId);

    int insertAddress(ShippingAddressDTO dto);
    
    List<ShippingAddressDTO> getAddressList(int memberId);
    
    int updateDefaultAddress(@Param("addressId") int addressId, @Param("memberId") int memberId);
    
    int deleteAddress(@Param("addressId") int addressId, @Param("memberId") int memberId);
}