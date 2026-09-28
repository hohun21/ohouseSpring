package com.ohouse.web.service.shopping.category;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ohouse.web.domain.shopping.category.CategoryDTO;
import com.ohouse.web.mapper.shopping.category.CategoryMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;

    @Override
    public List<CategoryDTO> viewCategory(long productId) {
        return categoryMapper.viewCategory(productId);
    }

    @Override
    public List<CategoryDTO> getLeafCategories(int categoryId) {
        return categoryMapper.getLeafCategories(categoryId);
    }

    @Override
    public List<CategoryDTO> getAllCategories() {
        return categoryMapper.getAllCategories();
    }

    @Override
    public List<CategoryDTO> getRootCategories() {
        return categoryMapper.getRootCategories();
    }

    @Override
    public List<CategoryDTO> getAllLeafCategories() {
        return categoryMapper.getAllLeafCategories();
    }

}