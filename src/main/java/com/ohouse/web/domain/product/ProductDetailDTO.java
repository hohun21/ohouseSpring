package com.ohouse.web.domain.product;



import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailDTO {

    private ProductDTO productDTO;
    private List<ProductImageDTO> imageDTOList;
    private List<OptionDTO> optionDTOList;
    private List<CategoryDTO> categoryDTOList;
}
