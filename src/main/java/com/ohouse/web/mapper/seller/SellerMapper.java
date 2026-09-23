package com.ohouse.web.mapper.seller;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ohouse.web.domain.seller.*;

public interface SellerMapper {
    
    // 1. 판매자 관리 및 목록 조회
    List<SellerDTO> getPendingSellers();
    int updateSellerStatus(@Param("sellerId") int sellerId, @Param("status") String status);
    int getPendingSellerCount();
    List<SellerDTO> getPendingSellersWithPaging(@Param("startRow") int startRow, @Param("endRow") int endRow);
    int getTotalSellerCount();
    List<SellerDTO> getSellerListWithPaging(@Param("startRow") int startRow, @Param("endRow") int endRow);
    int deleteSeller(@Param("sellerId") int sellerId);
    List<ProductDTO> getProductListByBrandId(@Param("brandId") int brandId);
    List<ProductDTO> getAllProductsForAdmin();

    // 2. 상품 등록 및 관리
    int getBrandId(@Param("brandName") String brandName);

    int insertProduct(ProductDTO dto);
    ProductDTO getProductById(@Param("productId") int productId);
    int updateProduct(ProductDTO dto);
    int deleteProduct(@Param("productId") int productId);
    
    int insertOptionGroup(OptionGroupDTO dto);
    int insertOptionValue(OptionValueDTO dto);
    List<OptionGroupDTO> getOptionGroups(@Param("productId") int productId);
    List<OptionValueDTO> getOptionValues(@Param("optionGroupId") int optionGroupId);
    void deleteOptionGroupsByProductId(@Param("productId") int productId);

    int insertProductOption(ProductOptionDTO dto);
    int insertProductOptionValue(ProductOptionValueDTO dto);
    List<ProductOptionDTO> getProductOptions(@Param("productId") int productId);
    int updateProductOption(ProductOptionDTO dto);
    int deleteProductOption(@Param("productOptionId") int productOptionId);
    int deleteProductOptionsByProductId(@Param("productId") int productId);
    
    int insertProductImage(ProductImageDTO imageDTO);
    int deleteProductImages(@Param("productId") int productId);
    int getTotalProductCount(@Param("brandId") int brandId);
    int getSoldOutProductCount(@Param("brandId") int brandId);
    
    int updateProductStatus(@Param("productId") int productId, @Param("status") String status);
    int getStopProductCount(@Param("brandId") int brandId);
    
    void resetAllOptionStocksToZero(@Param("productId") int productId);
    int updateOptionPriceAndStock(@Param("productId") int productId, @Param("skuName") String skuName, @Param("price") int price, @Param("stock") int stock);
}