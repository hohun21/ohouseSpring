package com.ohouse.web.service.shopping;

import java.util.List;
import com.ohouse.web.domain.shopping.ListingProduct;
import com.ohouse.web.mapper.shopping.FeaturedProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FeaturedProductService {
    private final FeaturedProductMapper mapper;
    @Transactional(readOnly = true)
    public List<ListingProduct> best() { return mapper.bestProducts(); }
    @Transactional(readOnly = true)
    public List<ListingProduct> only() { return mapper.onlyProducts(); }
}
