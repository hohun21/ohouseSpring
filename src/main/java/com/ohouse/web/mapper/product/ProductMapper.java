package com.ohouse.web.mapper.product;


import com.ohouse.web.domain.product.*;
import org.apache.ibatis.annotations.Param;

import java.sql.SQLException;
import java.util.List;

public interface ProductMapper {
    ProductDTO viewProduct(long product_id) throws SQLException;

    List<ProductDTO> allviewProduct() throws SQLException;

    List<ProductImageDTO> viewImage(long product_id) throws SQLException;

    ProductOptionDTO findProductOption(
            @Param("product_id") long productId,
            @Param("option_value_ids") List<Long> optionValueIds,
            @Param("option_value_count") int optionValueCount
    );

    List<OptionDTO> viewOption(long product_id) throws SQLException;

    List<CategoryDTO> viewCategory(long category_id) throws SQLException;

    List<ProductDTO> viewProductByCategories(List<Integer> categoryIds, String sort ) throws SQLException;
}
