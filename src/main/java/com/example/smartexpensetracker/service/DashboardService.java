package com.example.smartexpensetracker.service;

import com.example.smartexpensetracker.dto.CategoryExpenseResponse;
import com.example.smartexpensetracker.dto.DashboardSummaryResponse;
import com.example.smartexpensetracker.dto.MonthlySummaryResponse;
import com.example.smartexpensetracker.dto.TransactionResponse;
import com.example.smartexpensetracker.entity.Transaction;
import com.example.smartexpensetracker.entity.TransactionType;
import com.example.smartexpensetracker.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service calculating financial analytics and summaries for the dashboard and charts.
 */
@Service
public class DashboardService {

    private final TransactionRepository transactionRepository;
    private final TransactionService transactionService;

    public DashboardService(TransactionRepository transactionRepository,
                            TransactionService transactionService) {
        this.transactionRepository = transactionRepository;
        this.transactionService = transactionService;
    }

    /**
     * Computes the overall financial summary:
     * - Total Income
     * - Total Expenses
     * - Net Balance = Total Income - Total Expenses
     * - Recent 5 transactions
     */
    public DashboardSummaryResponse getSummary(Long userId) {
        BigDecimal totalIncome = transactionRepository.sumAmountByUserIdAndType(userId, TransactionType.INCOME);
        BigDecimal totalExpense = transactionRepository.sumAmountByUserIdAndType(userId, TransactionType.EXPENSE);

        if (totalIncome == null) totalIncome = BigDecimal.ZERO;
        if (totalExpense == null) totalExpense = BigDecimal.ZERO;

        BigDecimal netBalance = totalIncome.subtract(totalExpense);

        List<TransactionResponse> recent = transactionRepository
                .findTop5ByUserIdOrderByTransactionDateDescCreatedAtDesc(userId)
                .stream()
                .map(transactionService::mapToResponse)
                .collect(Collectors.toList());

        return new DashboardSummaryResponse(totalIncome, totalExpense, netBalance, recent);
    }

    /**
     * Calculates total expenses broken down by category, along with percentage share.
     */
    public List<CategoryExpenseResponse> getCategoryExpenses(Long userId) {
        List<Object[]> rawList = transactionRepository.getCategoryExpensesByUserId(userId);
        BigDecimal totalExpenses = transactionRepository.sumAmountByUserIdAndType(userId, TransactionType.EXPENSE);

        if (totalExpenses == null || totalExpenses.compareTo(BigDecimal.ZERO) == 0) {
            return Collections.emptyList();
        }

        List<CategoryExpenseResponse> result = new ArrayList<>();
        for (Object[] row : rawList) {
            String categoryName = (String) row[0];
            BigDecimal amount = (BigDecimal) row[1];
            double percentage = amount.multiply(BigDecimal.valueOf(100))
                    .divide(totalExpenses, 2, RoundingMode.HALF_UP)
                    .doubleValue();

            result.add(new CategoryExpenseResponse(categoryName, amount, percentage));
        }

        return result;
    }

    /**
     * Computes a 6-month historical financial breakdown (Income vs Expense) for trend charts.
     */
    public List<MonthlySummaryResponse> getMonthlySummary(Long userId) {
        YearMonth currentYearMonth = YearMonth.now();
        List<YearMonth> last6Months = new ArrayList<>();

        // Generate past 6 months chronologically
        for (int i = 5; i >= 0; i--) {
            last6Months.add(currentYearMonth.minusMonths(i));
        }

        LocalDate startDate = last6Months.get(0).atDay(1);
        LocalDate endDate = last6Months.get(5).atEndOfMonth();

        List<Transaction> transactions = transactionRepository
                .findByUserIdAndTransactionDateBetweenOrderByTransactionDateAsc(userId, startDate, endDate);

        // Group by YearMonth
        Map<YearMonth, List<Transaction>> groupedByMonth = transactions.stream()
                .collect(Collectors.groupingBy(t -> YearMonth.from(t.getTransactionDate())));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM yyyy");
        List<MonthlySummaryResponse> summaryList = new ArrayList<>();

        for (YearMonth ym : last6Months) {
            List<Transaction> monthTransactions = groupedByMonth.getOrDefault(ym, Collections.emptyList());

            BigDecimal income = monthTransactions.stream()
                    .filter(t -> t.getType() == TransactionType.INCOME)
                    .map(Transaction::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal expense = monthTransactions.stream()
                    .filter(t -> t.getType() == TransactionType.EXPENSE)
                    .map(Transaction::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal net = income.subtract(expense);

            summaryList.add(new MonthlySummaryResponse(
                    ym.format(formatter),
                    ym.getYear(),
                    ym.getMonthValue(),
                    income,
                    expense,
                    net
            ));
        }

        return summaryList;
    }
}
