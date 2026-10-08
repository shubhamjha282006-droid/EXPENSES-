package com.example.smartexpensetracker.dto;

import java.math.BigDecimal;

/**
 * DTO representing monthly financial comparison (Income vs Expense).
 */
public class MonthlySummaryResponse {

    private String monthLabel;
    private int year;
    private int monthValue;
    private BigDecimal income;
    private BigDecimal expense;
    private BigDecimal net;

    public MonthlySummaryResponse() {
    }

    public MonthlySummaryResponse(String monthLabel, int year, int monthValue,
                                  BigDecimal income, BigDecimal expense, BigDecimal net) {
        this.monthLabel = monthLabel;
        this.year = year;
        this.monthValue = monthValue;
        this.income = income;
        this.expense = expense;
        this.net = net;
    }

    public String getMonthLabel() {
        return monthLabel;
    }

    public void setMonthLabel(String monthLabel) {
        this.monthLabel = monthLabel;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonthValue() {
        return monthValue;
    }

    public void setMonthValue(int monthValue) {
        this.monthValue = monthValue;
    }

    public BigDecimal getIncome() {
        return income;
    }

    public void setIncome(BigDecimal income) {
        this.income = income;
    }

    public BigDecimal getExpense() {
        return expense;
    }

    public void setExpense(BigDecimal expense) {
        this.expense = expense;
    }

    public BigDecimal getNet() {
        return net;
    }

    public void setNet(BigDecimal net) {
        this.net = net;
    }
}
