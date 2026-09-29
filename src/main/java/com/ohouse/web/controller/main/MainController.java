package com.ohouse.web.controller.main;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.ohouse.web.domain.search.ProductSearchDTO;
import com.ohouse.web.domain.shopping.category.CategoryDTO;
import com.ohouse.web.service.main.MainService;
import com.ohouse.web.service.shopping.category.CategoryService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final MainService mainService;
    private final CategoryService categoryService;

    @GetMapping("/main.htm") 
    public String mainPage(Model model) throws Exception {

        List<ProductSearchDTO> randomProductList = mainService.getRandomProductList();
        model.addAttribute("randomProductList", randomProductList);
        model.addAttribute("activeMenu", "home");

        List<CategoryDTO> rootCategories = categoryService.getRootCategories();
        model.addAttribute("rootCategories", rootCategories);

        CategoryDTO furnitureCategory = null;
        CategoryDTO storageCategory = null;
        CategoryDTO kitchenCategory = null;

        if (rootCategories != null) {
            for (CategoryDTO category : rootCategories) {
                if ("가구".equals(category.getCategory_name())) {
                    furnitureCategory = category;
                } else if ("수납/정리".equals(category.getCategory_name())) {
                    storageCategory = category;
                } else if ("주방용품".equals(category.getCategory_name())) {
                    kitchenCategory = category;
                }
            }
        }

        model.addAttribute("furnitureCategory", furnitureCategory);
        model.addAttribute("storageCategory", storageCategory);
        model.addAttribute("kitchenCategory", kitchenCategory);

        return "main/main"; 
    }
}

