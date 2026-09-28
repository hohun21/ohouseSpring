package com.ohouse.web.mapper.search;

import java.util.List;
import com.ohouse.web.domain.search.KeyWordDTO;
import com.ohouse.web.domain.search.ProductSearchDTO;

public interface SearchMapper {
    void upsertKeyword(String keyword);
    List<ProductSearchDTO> selectProductsByKeyword(String keyword);
    void updateKeywordRanks();
    List<KeyWordDTO> selectTop10Keywords();
}