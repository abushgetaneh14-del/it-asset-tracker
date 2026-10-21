package com.project.it_asset_tracker.service;

import com.project.it_asset_tracker.dto.CategoryDTO;
import com.project.it_asset_tracker.entity.Category;
import com.project.it_asset_tracker.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(category -> new CategoryDTO(
                        category.getId(),
                        category.getName()
                ))
                .toList();
    }

    @Transactional
    public CategoryDTO createCategory(String name) {
        String normalizedName = name.trim();

        if (normalizedName.isBlank()) {
            throw new IllegalArgumentException("Category name is required");
        }

        if (categoryRepository.existsByName(normalizedName)) {
            throw new IllegalArgumentException(
                    "Category name already exists"
            );
        }

        Category category = new Category(normalizedName);
        Category savedCategory = categoryRepository.save(category);

        return new CategoryDTO(
                savedCategory.getId(),
                savedCategory.getName()
        );
    }
}