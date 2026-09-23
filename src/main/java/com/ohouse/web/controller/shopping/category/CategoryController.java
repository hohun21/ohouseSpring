package com.ohouse.web.controller.shopping.category;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ohouse.web.domain.shopping.category.CategoryDTO;
import com.ohouse.web.service.shopping.category.CategoryService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/shopping/category")
public class CategoryController {

	private final CategoryService categoryService;
	
	@GetMapping("/category.htm")
	public String category(
			@RequestParam(value = "category_id", required = false, defaultValue = "10000000") int categoryId,
			@RequestParam(value = "sort", required = false, defaultValue = "recommend") String sort,
			@RequestParam(value = "view", required = false, defaultValue = "all") String view,
			Model model) {
		
		List<CategoryDTO> categories = categoryService.getAllCategories();
		
		int selectedCategoryId = categoryId;
		
		CategoryDTO currentCategory = null;
		
		for(CategoryDTO category : categories) {
			if (category.getCategory_id() == selectedCategoryId) {
				currentCategory = category;
				break;
			} // if
		} // for
		
		boolean isOnly = "only".equals(view);
		
		CategoryDTO mainCategory = currentCategory;
		
		while (mainCategory != null && mainCategory.getParentId() != null) {
			int parentId = mainCategory.getParentId();
			
			CategoryDTO parentCategory = null;
			
			for(CategoryDTO category : categories) {
				if (category.getCategory_id() == parentId) {
					parentCategory = category;
					break;
				} // if
			} // for
			
			mainCategory = parentCategory;
		}// while
		
		String mainCategoryName = mainCategory != null ? mainCategory.getCategory_name() : "";
		
		List<CategoryDTO> leafCategories = categoryService.getLeafCategories(selectedCategoryId);
		
		List<Integer> categoryIds = new ArrayList<>();
		
		for (CategoryDTO category : leafCategories) {
			categoryIds.add(category.getCategory_id());
		} // for
		
		// ProductService에 구현필요
		// List<ProductDTO> products = productService.getProductListByCategories(categoryIds, sort);
		
		// List<ProductDTO>, List<OnlyDTO> List<ProductDTO>
		List<?> products = new ArrayList<>();
		List<?> onlyProducts = new ArrayList<>();
		List<?> bannerProducts = new ArrayList<>();
		
		model.addAttribute("activeMenu", "category");
		model.addAttribute("categories", categories);
		model.addAttribute("leafCategories", leafCategories);
		model.addAttribute("products", products);
		model.addAttribute("selectedCategoryId", selectedCategoryId);
		model.addAttribute("bannerProducts", bannerProducts);		
		model.addAttribute("OnlyProducts", onlyProducts);		
		model.addAttribute("isOnly", isOnly);
		model.addAttribute("mainCategoryName", mainCategoryName);
		model.addAttribute("mainCategoryId", mainCategory != null ? mainCategory.getCategory_id() : null);
		model.addAttribute("sort", sort);
		
		return "shopping/category/category";
		
	}
	
}
