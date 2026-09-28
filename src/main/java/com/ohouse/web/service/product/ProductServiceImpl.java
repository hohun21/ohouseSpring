package com.ohouse.web.service.product;

import com.ohouse.web.domain.product.*;
import com.ohouse.web.mapper.product.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;
    @Override
    public ProductDetailDTO productDetail(long product_id) throws SQLException {

        ProductDTO product =
                productMapper.viewProduct(product_id);

        List<ProductImageDTO> images =
                productMapper.viewImage(product_id);

        List<OptionDTO> options =
                productMapper.viewOption(product_id);

        List<CategoryDTO> categories =
                productMapper.viewCategory(product_id);

        ProductDetailDTO detail = new ProductDetailDTO();

        detail.setProductDTO(product);
        detail.setImageDTOList(images);
        detail.setOptionDTOList(options);
        detail.setCategoryDTOList(categories);


        return detail;
    }

    @Override
    public ProductOptionDTO productOption(long product_id,List<Long> option_value_ids) throws SQLException {
        return this.productMapper.findProductOption(product_id,option_value_ids,option_value_ids.size());
    }
}
