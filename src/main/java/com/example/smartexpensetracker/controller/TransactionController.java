package com.example.smartexpensetracker.controller;

import com.example.smartexpensetracker.dto.ApiResponse;
import com.example.smartexpensetracker.dto.TransactionRequest;
import com.example.smartexpensetracker.dto.TransactionResponse;
import com.example.smartexpensetracker.entity.TransactionType;
import com.example.smartexpensetracker.service.AuthService;
import com.example.smartexpensetracker.service.TransactionService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller providing CRUD operations, search, and filtering for financial transactions.
 */
@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final AuthService authService;

    public TransactionController(TransactionService transactionService, AuthService authService) {
        this.transactionService = transactionService;
        this.authService = authService;
    }

    /**
     * GET /api/transactions - Filter transactions or fetch all for the logged-in user.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getTransactions(
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String keyword,
            HttpSession session) {

        Long userId = authService.getAuthenticatedUserId(session);

        List<TransactionResponse> transactions = transactionService.filterTransactions(
                userId, type, categoryId, startDate, endDate, keyword);

        return ResponseEntity.ok(ApiResponse.success("Transactions retrieved successfully.", transactions));
    }

    /**
     * GET /api/transactions/{id} - Retrieve single transaction by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TransactionResponse>> getTransactionById(@PathVariable Long id,
                                                                               HttpSession session) {
        Long userId = authService.getAuthenticatedUserId(session);
        TransactionResponse response = transactionService.getTransactionById(userId, id);
        return ResponseEntity.ok(ApiResponse.success("Transaction retrieved successfully.", response));
    }

    /**
     * POST /api/transactions - Create new transaction.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<TransactionResponse>> createTransaction(
            @Valid @RequestBody TransactionRequest request,
            HttpSession session) {

        Long userId = authService.getAuthenticatedUserId(session);
        TransactionResponse response = transactionService.createTransaction(userId, request);

        return new ResponseEntity<>(
                ApiResponse.success("Transaction recorded successfully.", response),
                HttpStatus.CREATED
        );
    }

    /**
     * PUT /api/transactions/{id} - Update an existing transaction.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TransactionResponse>> updateTransaction(
            @PathVariable Long id,
            @Valid @RequestBody TransactionRequest request,
            HttpSession session) {

        Long userId = authService.getAuthenticatedUserId(session);
        TransactionResponse response = transactionService.updateTransaction(userId, id, request);

        return ResponseEntity.ok(ApiResponse.success("Transaction updated successfully.", response));
    }

    /**
     * DELETE /api/transactions/{id} - Delete an existing transaction.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTransaction(@PathVariable Long id,
                                                               HttpSession session) {
        Long userId = authService.getAuthenticatedUserId(session);
        transactionService.deleteTransaction(userId, id);

        return ResponseEntity.ok(ApiResponse.success("Transaction deleted successfully."));
    }
}
