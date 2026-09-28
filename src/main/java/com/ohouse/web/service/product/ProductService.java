package com.ohouse.web.service.product;


import com.ohouse.web.domain.product.ProductDTO;
import com.ohouse.web.domain.product.ProductDetailDTO;
import com.ohouse.web.domain.product.ProductOptionDTO;
import org.apache.ibatis.annotations.Param;

import java.sql.SQLException;
import java.util.List;

public interface ProductService {
    ProductDetailDTO productDetail(@Param("product_id") long product_id) throws SQLException;

    ProductOptionDTO productOption(@Param("product_id") long product_id
            ,@Param("option_values_ids") List<Long> option_value_ids) throws SQLException;
    List<ProductDTO> getProductListByCategories(List<Integer> categoryIds, String sort) throws SQLException;
}
