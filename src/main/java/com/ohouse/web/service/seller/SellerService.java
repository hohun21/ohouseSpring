package com.ohouse.web.service.seller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.ohouse.web.domain.seller.*;
import com.ohouse.web.mapper.seller.SellerMapper;

@Service
public class SellerService {

    @Autowired
    private SellerMapper sellerMapper;

    // 1-1. 상품 단건 조회
    public ProductDTO getProductById(int productId) {
        return sellerMapper.getProductById(productId);
    }
    
    // 1-2. 상품 옵션 목록 조회
    public List<ProductOptionDTO> getOptionsByProductId(int productId) {
        return sellerMapper.getProductOptions(productId);
    }

    // 2. 상품 등록 로직
    @Transactional
    public int registerProduct(ProductFormDTO form) {
        int brandId = sellerMapper.getBrandId(form.getBrandName());
        if (brandId == -1) {
            throw new RuntimeException("등록된 브랜드 정보를 찾을 수 없습니다: " + form.getBrandName());
        }
        
        ProductDTO productDTO = ProductDTO.builder()
                .categoryId(form.getCategoryId())
                .brandId(brandId)
                .productName(form.getProductName())
                .description(form.getDescription())
                .originalPrice(form.getOriginalPrice())
                .discountRate(form.getDiscountRate())
                .price(form.getPrice())
                .build();
                
        sellerMapper.insertProduct(productDTO);
        int productId = productDTO.getProductId(); // useGeneratedKeys로 자동 주입된 ID

        // 이미지 등록
        if (form.getImageUrls() != null && !form.getImageUrls().isEmpty()) {
            for (int i = 0; i < form.getImageUrls().size(); i++) {
                ProductImageDTO imageDTO = ProductImageDTO.builder()
                        .productId(productId)
                        .imageUrl(form.getImageUrls().get(i))
                        .imageType(form.getImageTypes().get(i))
                        .sortOrder(form.getSortOrders().get(i))
                        .build();
                sellerMapper.insertProductImage(imageDTO);
            }
        }

        Map<String, Integer> optionValueIdMap = new HashMap<>();
        int optionGroupCount = 0;

        // 필수 옵션 그룹 및 옵션 값 등록
        if (form.getOptionNames() != null && form.getOptionValues() != null) {
            for (int i = 0; i < form.getOptionNames().length; i++) {
                if (form.getOptionNames()[i].trim().equals("")) continue;

                optionGroupCount++;
                OptionGroupDTO groupDTO = OptionGroupDTO.builder()
                        .productId(productId)
                        .groupName(form.getOptionNames()[i])
                        .sortOrder(optionGroupCount)
                        .required(1)
                        .build();
                sellerMapper.insertOptionGroup(groupDTO);
                int optionGroupId = groupDTO.getOptionGroupId();
                
                String[] values = form.getOptionValues()[i].split(",");
                for (int j = 0; j < values.length; j++) {
                    String optName = values[j].trim();
                    if (optName.equals("")) continue;
                    
                    OptionValueDTO valueDTO = OptionValueDTO.builder()
                            .optionGroupId(optionGroupId)
                            .optionName(optName)
                            .sortOrder(j + 1)
                            .build();
                    sellerMapper.insertOptionValue(valueDTO);
                    optionValueIdMap.put(optName, valueDTO.getOptionValueId());
                }
            }
        }

        // 필수 옵션 조합 (SKU) 등록
        if (form.getSkuNames() != null) {
            for (int i = 0; i < form.getSkuNames().length; i++) {
                String currentSku = form.getSkuNames()[i];
                ProductOptionDTO skuDTO = ProductOptionDTO.builder()
                        .productId(productId)
                        .sku(currentSku)
                        .price(Integer.parseInt(form.getSkuPrices()[i]))
                        .stock(Integer.parseInt(form.getSkuStocks()[i]))
                        .status(Integer.parseInt(form.getSkuStocks()[i]) == 0 ? "SOLD_OUT" : "ACTIVE")
                        .build();
                
                sellerMapper.insertProductOption(skuDTO);
                int productOptionId = skuDTO.getProductOptionId();

                for (String optName : optionValueIdMap.keySet()) {
                    if (currentSku.contains(optName)) {
                        ProductOptionValueDTO mappingDTO = ProductOptionValueDTO.builder()
                                .productOptionId(productOptionId)
                                .optionValueId(optionValueIdMap.get(optName))
                                .build();
                        sellerMapper.insertProductOptionValue(mappingDTO);
                    }
                }
            }
        }

        // 추가 상품 등록
        if (form.getExtraNames() != null && form.getExtraNames().length > 0) {
            OptionGroupDTO extraGroupDTO = OptionGroupDTO.builder()
                    .productId(productId)
                    .groupName("추가상품")
                    .sortOrder(optionGroupCount + 1)
                    .required(0)
                    .build();
            sellerMapper.insertOptionGroup(extraGroupDTO);
            int extraGroupId = extraGroupDTO.getOptionGroupId();

            for (int i = 0; i < form.getExtraNames().length; i++) {
                if (form.getExtraNames()[i].trim().equals("")) continue;

                String extraName = form.getExtraNames()[i].trim();
                int extraPrice = Integer.parseInt(form.getExtraPrices()[i]);
                int extraStock = Integer.parseInt(form.getExtraStocks()[i]);

                OptionValueDTO extraValueDTO = OptionValueDTO.builder()
                        .optionGroupId(extraGroupId)
                        .optionName(extraName)
                        .sortOrder(i + 1)
                        .build();
                sellerMapper.insertOptionValue(extraValueDTO);
                int extraValueId = extraValueDTO.getOptionValueId();

                ProductOptionDTO extraSkuDTO = ProductOptionDTO.builder()
                        .productId(productId)
                        .sku("[추가상품] " + extraName)
                        .price(extraPrice)
                        .stock(extraStock)
                        .status(extraStock == 0 ? "SOLD_OUT" : "ACTIVE")
                        .build();
                sellerMapper.insertProductOption(extraSkuDTO);
                int productOptionId = extraSkuDTO.getProductOptionId();

                ProductOptionValueDTO mappingDTO = ProductOptionValueDTO.builder()
                        .productOptionId(productOptionId)
                        .optionValueId(extraValueId)
                        .build();
                sellerMapper.insertProductOptionValue(mappingDTO);
            }
        }

        return productId;
    }

    // 3. 상품 수정 로직 (ORA-02292 무결성 에러 예외 처리 반영)
    @Transactional(rollbackFor = Exception.class)
    public boolean updateProduct(ProductFormDTO form) {
        int brandId = sellerMapper.getBrandId(form.getBrandName());
        if (brandId == -1) {
            throw new RuntimeException("등록된 브랜드 정보를 찾을 수 없습니다: " + form.getBrandName());
        }
        
        ProductDTO productDTO = ProductDTO.builder()
                .productId(form.getProductId())
                .categoryId(form.getCategoryId())
                .brandId(brandId)
                .productName(form.getProductName())
                .description(form.getDescription())
                .originalPrice(form.getOriginalPrice())
                .discountRate(form.getDiscountRate())
                .price(form.getPrice())
                .build();
        sellerMapper.updateProduct(productDTO);

        // 이미지 교체
        if (form.getImageUrls() != null && !form.getImageUrls().isEmpty()) {
            sellerMapper.deleteProductImages(form.getProductId());
            for (int i = 0; i < form.getImageUrls().size(); i++) {
                ProductImageDTO imageDTO = ProductImageDTO.builder()
                        .productId(form.getProductId())
                        .imageUrl(form.getImageUrls().get(i))
                        .imageType(form.getImageTypes().get(i))
                        .sortOrder(form.getSortOrders().get(i))
                        .build();
                sellerMapper.insertProductImage(imageDTO);
            }
        }

        try {
            // 플랜 A: 옵션 전체 삭제 후 재생성 시도
            sellerMapper.deleteProductOptionsByProductId(form.getProductId());
            sellerMapper.deleteOptionGroupsByProductId(form.getProductId());

            Map<String, Integer> optionValueIdMap = new HashMap<>();
            int optionGroupCount = 0;

            if (form.getOptionNames() != null && form.getOptionValues() != null) {
                for (int i = 0; i < form.getOptionNames().length; i++) {
                    if (form.getOptionNames()[i].trim().equals("")) continue;
                    optionGroupCount++;
                    OptionGroupDTO groupDTO = OptionGroupDTO.builder().productId(form.getProductId()).groupName(form.getOptionNames()[i]).sortOrder(optionGroupCount).required(1).build();
                    sellerMapper.insertOptionGroup(groupDTO);
                    int optionGroupId = groupDTO.getOptionGroupId();
                    
                    String[] values = form.getOptionValues()[i].split(",");
                    for (int j = 0; j < values.length; j++) {
                        String optName = values[j].trim();
                        if (optName.equals("")) continue;
                        OptionValueDTO valueDTO = OptionValueDTO.builder().optionGroupId(optionGroupId).optionName(optName).sortOrder(j + 1).build();
                        sellerMapper.insertOptionValue(valueDTO);
                        optionValueIdMap.put(optName, valueDTO.getOptionValueId());
                    }
                }
            }

            if (form.getExtraNames() != null && form.getExtraNames().length > 0) {
                OptionGroupDTO extraGroupDTO = OptionGroupDTO.builder().productId(form.getProductId()).groupName("추가상품").sortOrder(optionGroupCount + 1).required(0).build();
                sellerMapper.insertOptionGroup(extraGroupDTO);
                int extraGroupId = extraGroupDTO.getOptionGroupId();

                for (int i = 0; i < form.getExtraNames().length; i++) {
                    if (form.getExtraNames()[i].trim().equals("")) continue;
                    String extraName = form.getExtraNames()[i].trim();
                    OptionValueDTO extraValDTO = OptionValueDTO.builder().optionGroupId(extraGroupId).optionName(extraName).sortOrder(i + 1).build();
                    sellerMapper.insertOptionValue(extraValDTO);
                    int extraValueId = extraValDTO.getOptionValueId();
                    
                    ProductOptionDTO extraSkuDTO = ProductOptionDTO.builder().productId(form.getProductId()).sku("[추가상품] " + extraName).price(Integer.parseInt(form.getExtraPrices()[i])).stock(Integer.parseInt(form.getExtraStocks()[i])).status(Integer.parseInt(form.getExtraStocks()[i]) == 0 ? "SOLD_OUT" : "ACTIVE").build();
                    sellerMapper.insertProductOption(extraSkuDTO);
                    sellerMapper.insertProductOptionValue(ProductOptionValueDTO.builder().productOptionId(extraSkuDTO.getProductOptionId()).optionValueId(extraValueId).build());
                }
            }

            if (form.getSkuNames() != null) {
                for (int i = 0; i < form.getSkuNames().length; i++) {
                    String currentSku = form.getSkuNames()[i];
                    ProductOptionDTO skuDTO = ProductOptionDTO.builder().productId(form.getProductId()).sku(currentSku).price(Integer.parseInt(form.getSkuPrices()[i])).stock(Integer.parseInt(form.getSkuStocks()[i])).status(Integer.parseInt(form.getSkuStocks()[i]) == 0 ? "SOLD_OUT" : "ACTIVE").build();
                    sellerMapper.insertProductOption(skuDTO);
                    int productOptionId = skuDTO.getProductOptionId();

                    for (String optName : optionValueIdMap.keySet()) {
                        if (currentSku.contains(optName)) {
                            sellerMapper.insertProductOptionValue(ProductOptionValueDTO.builder().productOptionId(productOptionId).optionValueId(optionValueIdMap.get(optName)).build());
                        }
                    }
                }
            }

        } catch (Exception e) {
            // ORA-02292 (주문 내역이 있어 외래키 제약조건에 걸릴 때)
            if (e.getMessage() != null && e.getMessage().contains("2292")) {
                System.out.println("주문 내역 발견! 옵션 삭제 취소 후 가격/재고 UPDATE 모드로 진입합니다.");
                
                // 스프링 트랜잭션 수동 롤백 유도 (이후 플랜 B 수행)
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                
                // 플랜 B 수행 (재고 및 가격 업데이트)
                sellerMapper.resetAllOptionStocksToZero(form.getProductId());

                if (form.getSkuNames() != null) {
                    for (int i = 0; i < form.getSkuNames().length; i++) {
                        sellerMapper.updateOptionPriceAndStock(
                            form.getProductId(), 
                            form.getSkuNames()[i], 
                            Integer.parseInt(form.getSkuPrices()[i]), 
                            Integer.parseInt(form.getSkuStocks()[i])
                        );
                    }
                }
                if (form.getExtraNames() != null) {
                    for (int i = 0; i < form.getExtraNames().length; i++) {
                        if (form.getExtraNames()[i].trim().equals("")) continue;
                        String extraName = "[추가상품] " + form.getExtraNames()[i].trim();
                        sellerMapper.updateOptionPriceAndStock(
                            form.getProductId(), 
                            extraName, 
                            Integer.parseInt(form.getExtraPrices()[i]), 
                            Integer.parseInt(form.getExtraStocks()[i])
                        );
                    }
                }
                return true; // 플랜 B 성공 처리
            } else {
                throw new RuntimeException(e);
            }
        }

        return true;
    }

    // 4. 상품 정보 삭제 로직
    @Transactional
    public boolean deleteProduct(int productId) {
        int result = sellerMapper.deleteProduct(productId);
        return result > 0;
    }
    
    // 5. 상품 수정 폼을 위한 옵션 포맷팅
    public List<Map<String, String>> getOptionItemsForEdit(int productId) {
        List<Map<String, String>> optionItems = new ArrayList<>();
        List<OptionGroupDTO> groups = sellerMapper.getOptionGroups(productId);
        
        for (OptionGroupDTO group : groups) {
            if ("추가상품".equals(group.getGroupName())) continue;
            
            List<OptionValueDTO> values = sellerMapper.getOptionValues(group.getOptionGroupId());
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < values.size(); i++) {
                sb.append(values.get(i).getOptionName());
                if (i < values.size() - 1) sb.append(",");
            }
            Map<String, String> item = new HashMap<>();
            item.put("groupName", group.getGroupName());
            item.put("valuesStr", sb.toString());
            optionItems.add(item);
        }
        return optionItems;
    }
    
    // 6. 판매자 대시보드 통계
    public Map<String, Integer> getDashboardStats(String brandName) {
        Map<String, Integer> stats = new HashMap<>();
        int brandId = sellerMapper.getBrandId(brandName);
        
        int totalCount = sellerMapper.getTotalProductCount(brandId);
        int soldOutCount = sellerMapper.getSoldOutProductCount(brandId);
        int stopCount = sellerMapper.getStopProductCount(brandId); 
        
        int onSaleCount = totalCount - soldOutCount - stopCount;
        if (onSaleCount < 0) onSaleCount = 0;
        
        stats.put("totalCount", totalCount);
        stats.put("soldOutCount", soldOutCount);
        stats.put("onSaleCount", onSaleCount);
        stats.put("stopCount", stopCount); 
        
        return stats;
    }

    public List<SellerDTO> getPendingSellers() {
        return sellerMapper.getPendingSellers();
    }

    @Transactional
    public boolean updateSellerStatus(int sellerId, String status) {
        int count = sellerMapper.updateSellerStatus(sellerId, status);
        return count > 0;
    }
    
    @Transactional
    public boolean updateProductStatus(int productId, String status) {
        int count = sellerMapper.updateProductStatus(productId, status);
        return count > 0;
    }
    
    public List<ProductDTO> getProductListByBrandName(String brandName) {
        List<ProductDTO> list = new ArrayList<>();
        int brandId = sellerMapper.getBrandId(brandName);
        if (brandId != -1) {
            list = sellerMapper.getProductListByBrandId(brandId);
        }
        return list;
    }
}