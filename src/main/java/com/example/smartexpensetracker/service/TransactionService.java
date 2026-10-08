package com.example.smartexpensetracker.service;

import com.example.smartexpensetracker.dto.TransactionRequest;
import com.example.smartexpensetracker.dto.TransactionResponse;
import com.example.smartexpensetracker.entity.Category;
import com.example.smartexpensetracker.entity.Transaction;
import com.example.smartexpensetracker.entity.TransactionType;
import com.example.smartexpensetracker.entity.User;
import com.example.smartexpensetracker.exception.BadRequestException;
import com.example.smartexpensetracker.exception.ResourceNotFoundException;
import com.example.smartexpensetracker.repository.TransactionRepository;
import com.example.smartexpensetracker.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service managing all financial transaction operations.
 * Enforces user ownership, business validations, and data persistence.
 */
@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final CategoryService categoryService;

    public TransactionService(TransactionRepository transactionRepository,
                              UserRepository userRepository,
                              CategoryService categoryService) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.categoryService = categoryService;
    }

    /**
     * Records a new income or expense transaction for the authenticated user.
     */
    @Transactional
    public TransactionResponse createTransaction(Long userId, TransactionRequest request) {
        validateTransactionRequest(request);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        Category category = categoryService.getCategoryEntity(request.getCategoryId());

        // Validate that category type matches transaction type
        if (category.getType() != request.getType()) {
            throw new BadRequestException("Selected category '" + category.getName() +
                    "' belongs to " + category.getType() + " but transaction is " + request.getType());
        }

        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setCategory(category);
        transaction.setAmount(request.getAmount());
        transaction.setType(request.getType());
        transaction.setDescription(request.getDescription() != null ? request.getDescription().trim() : null);
        transaction.setTransactionDate(request.getTransactionDate());

        Transaction saved = transactionRepository.save(transaction);
        return mapToResponse(saved);
    }

    /**
     * Updates an existing transaction owned by the authenticated user.
     */
    @Transactional
    public TransactionResponse updateTransaction(Long userId, Long transactionId, TransactionRequest request) {
        validateTransactionRequest(request);

        Transaction transaction = transactionRepository.findByIdAndUserId(transactionId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with ID: " + transactionId +
                        " or you do not have permission to modify it"));

        Category category = categoryService.getCategoryEntity(request.getCategoryId());

        if (category.getType() != request.getType()) {
            throw new BadRequestException("Selected category '" + category.getName() +
                    "' belongs to " + category.getType() + " but transaction is " + request.getType());
        }

        transaction.setCategory(category);
        transaction.setAmount(request.getAmount());
        transaction.setType(request.getType());
        transaction.setDescription(request.getDescription() != null ? request.getDescription().trim() : null);
        transaction.setTransactionDate(request.getTransactionDate());

        Transaction updated = transactionRepository.save(transaction);
        return mapToResponse(updated);
    }

    /**
     * Deletes a transaction owned by the authenticated user.
     */
    @Transactional
    public void deleteTransaction(Long userId, Long transactionId) {
        Transaction transaction = transactionRepository.findByIdAndUserId(transactionId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with ID: " + transactionId +
                        " or you do not have permission to delete it"));

        transactionRepository.delete(transaction);
    }

    /**
     * Retrieves a single transaction by ID for the authenticated user.
     */
    public TransactionResponse getTransactionById(Long userId, Long transactionId) {
        Transaction transaction = transactionRepository.findByIdAndUserId(transactionId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with ID: " + transactionId));

        return mapToResponse(transaction);
    }

    /**
     * Retrieves all transactions for the authenticated user.
     */
    public List<TransactionResponse> getAllTransactions(Long userId) {
        return transactionRepository.findByUserIdOrderByTransactionDateDescCreatedAtDesc(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Filters transactions by type, category, date range, and keyword search.
     */
    public List<TransactionResponse> filterTransactions(Long userId, TransactionType type,
                                                        Long categoryId, LocalDate startDate,
                                                        LocalDate endDate, String keyword) {
        String cleanKeyword = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;
        return transactionRepository.filterTransactions(userId, type, categoryId, startDate, endDate, cleanKeyword)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private void validateTransactionRequest(TransactionRequest request) {
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Transaction amount must be strictly greater than zero");
        }
        if (request.getType() == null) {
            throw new BadRequestException("Transaction type (INCOME or EXPENSE) is required");
        }
        if (request.getCategoryId() == null) {
            throw new BadRequestException("Category must be selected");
        }
        if (request.getTransactionDate() == null) {
            throw new BadRequestException("Transaction date is required");
        }
    }

    public TransactionResponse mapToResponse(Transaction t) {
        return new TransactionResponse(
                t.getId(),
                t.getUser().getId(),
                t.getCategory().getId(),
                t.getCategory().getName(),
                t.getAmount(),
                t.getType(),
                t.getDescription(),
                t.getTransactionDate(),
                t.getCreatedAt()
        );
    }
}
