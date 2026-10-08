package com.example.smartexpensetracker.controller;

import com.example.smartexpensetracker.dto.ApiResponse;
import com.example.smartexpensetracker.dto.CategoryExpenseResponse;
import com.example.smartexpensetracker.dto.DashboardSummaryResponse;
import com.example.smartexpensetracker.dto.MonthlySummaryResponse;
import com.example.smartexpensetracker.service.AuthService;
import com.example.smartexpensetracker.service.DashboardService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller delivering dashboard summary KPIs and analytics data for Chart.js.
 */
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;
    private final AuthService authService;

    public DashboardController(DashboardService dashboardService, AuthService authService) {
        this.dashboardService = dashboardService;
        this.authService = authService;
    }

    /**
     * GET /api/dashboard/summary - Key KPIs (Income, Expense, Net Balance) and recent 5 transactions.
     */
    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<DashboardSummaryResponse>> getSummary(HttpSession session) {
        Long userId = authService.getAuthenticatedUserId(session);
        DashboardSummaryResponse response = dashboardService.getSummary(userId);
        return ResponseEntity.ok(ApiResponse.success("Dashboard summary retrieved.", response));
    }

    /**
     * GET /api/dashboard/category-expenses - Expense sums by category for doughnut/pie chart.
     */
    @GetMapping("/category-expenses")
    public ResponseEntity<ApiResponse<List<CategoryExpenseResponse>>> getCategoryExpenses(HttpSession session) {
        Long userId = authService.getAuthenticatedUserId(session);
        List<CategoryExpenseResponse> response = dashboardService.getCategoryExpenses(userId);
        return ResponseEntity.ok(ApiResponse.success("Category expense analytics retrieved.", response));
    }

    /**
     * GET /api/dashboard/monthly-summary - 6-month historical Income vs Expense for bar/line charts.
     */
    @GetMapping("/monthly-summary")
    public ResponseEntity<ApiResponse<List<MonthlySummaryResponse>>> getMonthlySummary(HttpSession session) {
        Long userId = authService.getAuthenticatedUserId(session);
        List<MonthlySummaryResponse> response = dashboardService.getMonthlySummary(userId);
        return ResponseEntity.ok(ApiResponse.success("Monthly summary analytics retrieved.", response));
    }
}
