package com.project.it_asset_tracker.controller;

import com.project.it_asset_tracker.dto.CategoryDTO;
import com.project.it_asset_tracker.service.CategoryService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/categories")
@Validated
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public List<CategoryDTO> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public CategoryDTO createCategory(
            @RequestBody Map<String, String> request) {

        String name = request.get("name");

        validateName(name);

        return categoryService.createCategory(name);
    }

    private void validateName(
            @NotBlank(message = "Category name is required")
            @Size(max = 100, message = "Category name must not exceed 100 characters")
            String name) {

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Category name is required");
        }

        if (name.trim().length() > 100) {
            throw new IllegalArgumentException(
                    "Category name must not exceed 100 characters"
            );
        }
    }
}