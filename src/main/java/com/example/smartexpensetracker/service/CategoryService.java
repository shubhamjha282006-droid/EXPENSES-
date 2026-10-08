package com.example.smartexpensetracker.service;

import com.example.smartexpensetracker.dto.CategoryResponse;
import com.example.smartexpensetracker.entity.Category;
import com.example.smartexpensetracker.entity.TransactionType;
import com.example.smartexpensetracker.exception.BadRequestException;
import com.example.smartexpensetracker.exception.ResourceNotFoundException;
import com.example.smartexpensetracker.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service managing income and expense categories.
 */
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * Get all categories sorted by name.
     */
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get categories filtered by type (INCOME or EXPENSE).
     */
    public List<CategoryResponse> getCategoriesByType(TransactionType type) {
        return categoryRepository.findByTypeOrderByNameAsc(type).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Retrieve category entity by ID.
     */
    public Category getCategoryEntity(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + id));
    }

    /**
     * Dynamically add a new category if needed.
     */
    @Transactional
    public CategoryResponse createCategory(String name, TransactionType type) {
        String trimmed = name.trim();
        if (categoryRepository.existsByNameAndType(trimmed, type)) {
            throw new BadRequestException("Category '" + trimmed + "' already exists for " + type);
        }

        Category category = new Category(trimmed, type);
        Category saved = categoryRepository.save(category);
        return mapToResponse(saved);
    }

    private CategoryResponse mapToResponse(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getType()
        );
    }
}
