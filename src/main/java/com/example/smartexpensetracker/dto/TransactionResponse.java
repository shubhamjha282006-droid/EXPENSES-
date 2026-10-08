package com.example.smartexpensetracker.dto;

import com.example.smartexpensetracker.entity.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO representing transaction data returned to the client.
 */
public class TransactionResponse {

    private Long id;
    private Long userId;
    private Long categoryId;
    private String categoryName;
    private BigDecimal amount;
    private TransactionType type;
    private String description;
    private LocalDate transactionDate;
    private LocalDateTime createdAt;

    public TransactionResponse() {
    }

    public TransactionResponse(Long id, Long userId, Long categoryId, String categoryName,
                               BigDecimal amount, TransactionType type, String description,
                               LocalDate transactionDate, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.amount = amount;
        this.type = type;
        this.description = description;
        this.transactionDate = transactionDate;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
