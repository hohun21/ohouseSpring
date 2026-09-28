package com.ohouse.web.service.shopping;

import java.util.List;
import com.ohouse.web.domain.shopping.ShoppingProductListDTO;
import com.ohouse.web.mapper.shopping.ShoppingMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ShoppingService {
    private final ShoppingMapper mapper;
    
    @Transactional(readOnly = true)
    public List<ShoppingProductListDTO> best() { return mapper.bestProducts(); }
    @Transactional(readOnly = true)
    public List<ShoppingProductListDTO> only() { return mapper.onlyProducts(); }
}
