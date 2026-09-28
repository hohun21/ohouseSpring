package com.ohouse.web.service.main;

import com.ohouse.web.domain.search.ProductSearchDTO;
import com.ohouse.web.mapper.main.MainMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MainService {

    private final MainMapper mainMapper;

    public List<ProductSearchDTO> getRandomProductList() {
        return mainMapper.selectRandomProducts();
    }
}