package com.example.smartexpensetracker.repository;

import com.example.smartexpensetracker.entity.Transaction;
import com.example.smartexpensetracker.entity.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for Transaction entity.
 * Provides custom query methods for search, filter, and analytics aggregations.
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /**
     * Retrieve all transactions for a user sorted by transaction date and creation time descending.
     */
    List<Transaction> findByUserIdOrderByTransactionDateDescCreatedAtDesc(Long userId);

    /**
     * Retrieve recent transactions for a user (top 5).
     */
    List<Transaction> findTop5ByUserIdOrderByTransactionDateDescCreatedAtDesc(Long userId);

    /**
     * Find a transaction by ID and User ID to enforce user data ownership.
     */
    Optional<Transaction> findByIdAndUserId(Long id, Long userId);

    /**
     * Filter transactions by optional type, category, date range, and keyword search.
     */
    @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId " +
           "AND (:type IS NULL OR t.type = :type) " +
           "AND (:categoryId IS NULL OR t.category.id = :categoryId) " +
           "AND (:startDate IS NULL OR t.transactionDate >= :startDate) " +
           "AND (:endDate IS NULL OR t.transactionDate <= :endDate) " +
           "AND (:keyword IS NULL OR LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY t.transactionDate DESC, t.createdAt DESC")
    List<Transaction> filterTransactions(
            @Param("userId") Long userId,
            @Param("type") TransactionType type,
            @Param("categoryId") Long categoryId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("keyword") String keyword);

    /**
     * Calculate total sum of amounts for a user and transaction type (INCOME or EXPENSE).
     */
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.user.id = :userId AND t.type = :type")
    BigDecimal sumAmountByUserIdAndType(@Param("userId") Long userId, @Param("type") TransactionType type);

    /**
     * Retrieve category-wise expense totals for a specific user.
     * Returns Category Name and Total Amount Spent.
     */
    @Query("SELECT t.category.name, SUM(t.amount) FROM Transaction t " +
           "WHERE t.user.id = :userId AND t.type = com.example.smartexpensetracker.entity.TransactionType.EXPENSE " +
           "GROUP BY t.category.name ORDER BY SUM(t.amount) DESC")
    List<Object[]> getCategoryExpensesByUserId(@Param("userId") Long userId);

    /**
     * Fetch all transactions for a user within a specific date range.
     */
    List<Transaction> findByUserIdAndTransactionDateBetweenOrderByTransactionDateAsc(
            Long userId, LocalDate startDate, LocalDate endDate);
}
