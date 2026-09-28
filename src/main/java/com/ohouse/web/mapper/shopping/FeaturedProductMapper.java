package com.ohouse.web.mapper.shopping;

import java.util.List;
import com.ohouse.web.domain.shopping.ListingProduct;

public interface FeaturedProductMapper {
    List<ListingProduct> bestProducts();
    List<ListingProduct> onlyProducts();
}
