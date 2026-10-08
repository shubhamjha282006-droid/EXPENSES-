package com.example.smartexpensetracker.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO representing aggregate totals for the main dashboard view.
 */
public class DashboardSummaryResponse {

    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal netBalance;
    private List<TransactionResponse> recentTransactions;

    public DashboardSummaryResponse() {
    }

    public DashboardSummaryResponse(BigDecimal totalIncome, BigDecimal totalExpense,
                                    BigDecimal netBalance, List<TransactionResponse> recentTransactions) {
        this.totalIncome = totalIncome;
        this.totalExpense = totalExpense;
        this.netBalance = netBalance;
        this.recentTransactions = recentTransactions;
    }

    public BigDecimal getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(BigDecimal totalIncome) {
        this.totalIncome = totalIncome;
    }

    public BigDecimal getTotalExpense() {
        return totalExpense;
    }

    public void setTotalExpense(BigDecimal totalExpense) {
        this.totalExpense = totalExpense;
    }

    public BigDecimal getNetBalance() {
        return netBalance;
    }

    public void setNetBalance(BigDecimal netBalance) {
        this.netBalance = netBalance;
    }

    public List<TransactionResponse> getRecentTransactions() {
        return recentTransactions;
    }

    public void setRecentTransactions(List<TransactionResponse> recentTransactions) {
        this.recentTransactions = recentTransactions;
    }
}
