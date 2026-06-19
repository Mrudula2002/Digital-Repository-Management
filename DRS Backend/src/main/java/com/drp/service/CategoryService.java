package com.drp.service;

import com.drp.dto.request.CategoryRequest;
import com.drp.dto.response.CategoryResponse;
import com.drp.entity.ActivityAction;
import com.drp.entity.Category;
import com.drp.entity.User;
import com.drp.exception.BadRequestException;
import com.drp.exception.ResourceNotFoundException;
import com.drp.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ActivityLogService activityLogService;

    public CategoryService(CategoryRepository categoryRepository, ActivityLogService activityLogService) {
        this.categoryRepository = categoryRepository;
        this.activityLogService = activityLogService;
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(CategoryResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public Category findCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
    }

    @Transactional
    public CategoryResponse createCategory(CategoryRequest request, User actor) {
        if (categoryRepository.existsByNameIgnoreCase(request.getName())) {
            throw new BadRequestException("Category already exists");
        }

        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());

        Category saved = categoryRepository.save(category);
        activityLogService.log(actor, ActivityAction.CREATE_CATEGORY, "Category", saved.getId(),
                "Created category: " + saved.getName());

        return CategoryResponse.from(saved);
    }

    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryRequest request, User actor) {
        Category category = findCategoryById(id);

        categoryRepository.findByNameIgnoreCase(request.getName())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new BadRequestException("Category name already exists");
                });

        category.setName(request.getName());
        category.setDescription(request.getDescription());

        Category saved = categoryRepository.save(category);
        activityLogService.log(actor, ActivityAction.UPDATE_CATEGORY, "Category", saved.getId(),
                "Updated category: " + saved.getName());

        return CategoryResponse.from(saved);
    }

    @Transactional
    public void deleteCategory(Long id, User actor) {
        Category category = findCategoryById(id);
        categoryRepository.delete(category);
        activityLogService.log(actor, ActivityAction.DELETE_CATEGORY, "Category", id,
                "Deleted category: " + category.getName());
    }
}
