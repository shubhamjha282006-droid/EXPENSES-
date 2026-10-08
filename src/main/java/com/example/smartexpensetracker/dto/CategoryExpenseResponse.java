package com.example.smartexpensetracker.dto;

import java.math.BigDecimal;

/**
 * DTO representing an expense category aggregated amount and its percentage of overall expenses.
 */
public class CategoryExpenseResponse {

    private String categoryName;
    private BigDecimal totalAmount;
    private Double percentage;

    public CategoryExpenseResponse() {
    }

    public CategoryExpenseResponse(String categoryName, BigDecimal totalAmount, Double percentage) {
        this.categoryName = categoryName;
        this.totalAmount = totalAmount;
        this.percentage = percentage;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Double getPercentage() {
        return percentage;
    }

    public void setPercentage(Double percentage) {
        this.percentage = percentage;
    }
}
