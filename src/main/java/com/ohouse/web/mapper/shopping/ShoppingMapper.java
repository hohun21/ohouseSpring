package com.ohouse.web.mapper.shopping;

import java.util.List;
import com.ohouse.web.domain.shopping.ShoppingProductListDTO;

public interface ShoppingMapper {
    List<ShoppingProductListDTO> bestProducts();
    List<ShoppingProductListDTO> onlyProducts();
}
