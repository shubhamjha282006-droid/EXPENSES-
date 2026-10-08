package com.example.smartexpensetracker;

import com.example.smartexpensetracker.dto.DashboardSummaryResponse;
import com.example.smartexpensetracker.dto.RegisterRequest;
import com.example.smartexpensetracker.dto.TransactionRequest;
import com.example.smartexpensetracker.entity.TransactionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests verifying core business logic, validations, and security hashing.
 */
class SmartExpenseTrackerApplicationTests {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Test
    @DisplayName("Verify BCrypt hashes are salted, non-plaintext, and verifiable")
    void testBCryptPasswordHashing() {
        String rawPassword = "studentSecretPassword123";
        String hash = passwordEncoder.encode(rawPassword);

        assertNotNull(hash);
        assertNotEquals(rawPassword, hash, "Password must not be stored in plain text");
        assertTrue(hash.startsWith("$2a$") || hash.startsWith("$2b$"), "BCrypt prefix expected");
        assertTrue(passwordEncoder.matches(rawPassword, hash), "Password verification must succeed");
        assertFalse(passwordEncoder.matches("wrongPassword", hash), "Incorrect password must fail");
    }

    @Test
    @DisplayName("Verify Dashboard Net Balance calculation formula: Income - Expense")
    void testNetBalanceCalculation() {
        BigDecimal income = new BigDecimal("45000.00");
        BigDecimal expense = new BigDecimal("12500.50");
        BigDecimal expectedBalance = income.subtract(expense); // 32499.50

        DashboardSummaryResponse summary = new DashboardSummaryResponse(
                income,
                expense,
                expectedBalance,
                null
        );

        assertEquals(new BigDecimal("45000.00"), summary.getTotalIncome());
        assertEquals(new BigDecimal("12500.50"), summary.getTotalExpense());
        assertEquals(new BigDecimal("32499.50"), summary.getNetBalance());
    }

    @Test
    @DisplayName("Verify Negative Net Balance when expenses exceed income")
    void testNegativeNetBalance() {
        BigDecimal income = new BigDecimal("10000.00");
        BigDecimal expense = new BigDecimal("15000.00");
        BigDecimal expectedBalance = income.subtract(expense); // -5000.00

        DashboardSummaryResponse summary = new DashboardSummaryResponse(
                income,
                expense,
                expectedBalance,
                null
        );

        assertTrue(summary.getNetBalance().compareTo(BigDecimal.ZERO) < 0,
                "Net balance must be negative when expenses exceed income");
        assertEquals(new BigDecimal("-5000.00"), summary.getNetBalance());
    }

    @Test
    @DisplayName("Verify TransactionRequest DTO fields and positive amount integrity")
    void testTransactionRequestIntegrity() {
        TransactionRequest request = new TransactionRequest(
                1L,
                new BigDecimal("250.75"),
                TransactionType.EXPENSE,
                "Bus ticket",
                LocalDate.now()
        );

        assertEquals(1L, request.getCategoryId());
        assertEquals(TransactionType.EXPENSE, request.getType());
        assertTrue(request.getAmount().compareTo(BigDecimal.ZERO) > 0);
        assertEquals("Bus ticket", request.getDescription());
    }

    @Test
    @DisplayName("Verify RegisterRequest password confirmation check")
    void testRegisterRequestPasswordsMatch() {
        RegisterRequest validRequest = new RegisterRequest(
                "student_coder",
                "student@college.edu",
                "secret123",
                "secret123"
        );
        assertEquals(validRequest.getPassword(), validRequest.getConfirmPassword());

        RegisterRequest mismatchRequest = new RegisterRequest(
                "student_coder",
                "student@college.edu",
                "secret123",
                "differentPassword"
        );
        assertNotEquals(mismatchRequest.getPassword(), mismatchRequest.getConfirmPassword());
    }
}
