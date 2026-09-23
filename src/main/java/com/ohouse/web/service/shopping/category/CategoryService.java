package com.ohouse.web.service.shopping.category;

import java.util.List;

import com.ohouse.web.domain.shopping.category.CategoryDTO;

public interface CategoryService {

    List<CategoryDTO> viewCategory(long productId);

    List<CategoryDTO> getLeafCategories(int categoryId);

    List<CategoryDTO> getAllCategories();

    List<CategoryDTO> getRootCategories();

    List<CategoryDTO> getAllLeafCategories();

}