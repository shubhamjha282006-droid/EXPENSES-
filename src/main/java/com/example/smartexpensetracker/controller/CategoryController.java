package com.example.smartexpensetracker.controller;

import com.example.smartexpensetracker.dto.ApiResponse;
import com.example.smartexpensetracker.dto.CategoryResponse;
import com.example.smartexpensetracker.entity.TransactionType;
import com.example.smartexpensetracker.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller for retrieving and managing transaction categories.
 */
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * GET /api/categories - List categories (optionally filtered by type: INCOME or EXPENSE).
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getCategories(
            @RequestParam(required = false) TransactionType type) {
        List<CategoryResponse> categories = (type != null)
                ? categoryService.getCategoriesByType(type)
                : categoryService.getAllCategories();

        return ResponseEntity.ok(ApiResponse.success("Categories retrieved successfully.", categories));
    }

    /**
     * POST /api/categories - Dynamically create a category.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(
            @RequestBody Map<String, String> body) {
        String name = body.get("name");
        String typeStr = body.get("type");

        if (name == null || name.trim().isEmpty() || typeStr == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Name and Type are required."));
        }

        TransactionType type = TransactionType.valueOf(typeStr.toUpperCase());
        CategoryResponse response = categoryService.createCategory(name, type);
        return new ResponseEntity<>(
                ApiResponse.success("Category created successfully.", response),
                HttpStatus.CREATED
        );
    }
}
