package com.drp.controller;

import com.drp.dto.request.CategoryRequest;
import com.drp.dto.response.ApiResponse;
import com.drp.dto.response.CategoryResponse;
import com.drp.entity.User;
import com.drp.security.SecurityUtils;
import com.drp.service.CategoryService;
import com.drp.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final UserService userService;

    public CategoryController(CategoryService categoryService, UserService userService) {
        this.categoryService = categoryService;
        this.userService = userService;
    }

    @GetMapping
    public ApiResponse<List<CategoryResponse>> getAllCategories() {
        return ApiResponse.success(categoryService.getAllCategories());
    }

    @PostMapping
    public ApiResponse<CategoryResponse> createCategory(@Valid @RequestBody CategoryRequest request) {
        User actor = userService.findUserByUsername(SecurityUtils.getCurrentUsername());
        return ApiResponse.success("Category created", categoryService.createCategory(request, actor));
    }

    @PutMapping("/{id}")
    public ApiResponse<CategoryResponse> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request) {
        User actor = userService.findUserByUsername(SecurityUtils.getCurrentUsername());
        return ApiResponse.success("Category updated", categoryService.updateCategory(id, request, actor));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteCategory(@PathVariable Long id) {
        User actor = userService.findUserByUsername(SecurityUtils.getCurrentUsername());
        categoryService.deleteCategory(id, actor);
        return ApiResponse.success("Category deleted", null);
    }
}
