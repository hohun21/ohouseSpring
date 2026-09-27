package com.ohouse.web.controller.search;

import java.util.List;
import com.ohouse.web.domain.search.KeyWordDTO;
import com.ohouse.web.domain.search.ProductSearchDTO;
import com.ohouse.web.service.search.SearchService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @GetMapping("/search.htm")
    public String searchProduct(@RequestParam(value = "keyword", required = false) String keyword, Model model) {
        
        if (keyword == null || keyword.trim().length() < 2) {
            model.addAttribute("keyword", keyword != null ? keyword : "");
            model.addAttribute("productList", null);
            return "store/search_result"; 
        }

        keyword = keyword.trim();

        searchService.registerKeyword(keyword);
        List<ProductSearchDTO> productList = searchService.getProductsByKeyword(keyword);

        model.addAttribute("keyword", keyword);
        model.addAttribute("productList", productList);

        return "store/search_result";
    }

    @ResponseBody
    @GetMapping("/search/top10.ajax")
    public List<KeyWordDTO> getTop10Keywords() {
        return searchService.getTop10Keywords();
    }
}