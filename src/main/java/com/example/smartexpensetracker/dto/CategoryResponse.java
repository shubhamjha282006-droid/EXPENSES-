package com.example.smartexpensetracker.dto;

import com.example.smartexpensetracker.entity.TransactionType;

/**
 * DTO representing category details returned to the frontend.
 */
public class CategoryResponse {

    private Long id;
    private String name;
    private TransactionType type;

    public CategoryResponse() {
    }

    public CategoryResponse(Long id, String name, TransactionType type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }
}
