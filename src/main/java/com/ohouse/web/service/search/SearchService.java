package com.ohouse.web.service.search;

import java.util.List;
import com.ohouse.web.domain.search.KeyWordDTO;
import com.ohouse.web.domain.search.ProductSearchDTO;
import com.ohouse.web.mapper.search.SearchMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final SearchMapper searchMapper;

    @Transactional
    public void registerKeyword(String keyword) {
        searchMapper.upsertKeyword(keyword);
        searchMapper.updateKeywordRanks();
    }

    public List<ProductSearchDTO> getProductsByKeyword(String keyword) {
        return searchMapper.selectProductsByKeyword(keyword);
    }

    public List<KeyWordDTO> getTop10Keywords() {
        return searchMapper.selectTop10Keywords();
    }
}