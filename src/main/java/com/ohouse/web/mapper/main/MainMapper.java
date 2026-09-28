package com.ohouse.web.mapper.main;

import com.ohouse.web.domain.search.ProductSearchDTO;
import java.util.List;

public interface MainMapper {
    
    List<ProductSearchDTO> selectRandomProducts();
    
}