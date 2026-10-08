package com.example.smartexpensetracker.controller;

import com.example.smartexpensetracker.config.AuthInterceptor;
import com.example.smartexpensetracker.dto.ApiResponse;
import com.example.smartexpensetracker.service.DatabaseService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller exposing MySQL table browsing, schema metadata, and query console to the web interface.
 */
@RestController
@RequestMapping("/api/database")
public class DatabaseController {

    private final DatabaseService databaseService;

    public DatabaseController(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    /**
     * GET /api/database/info - Status, MySQL version, table counts.
     */
    @GetMapping("/info")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDatabaseInfo() {
        Map<String, Object> info = databaseService.getDatabaseInfo();
        return ResponseEntity.ok(ApiResponse.success("Database information retrieved.", info));
    }

    /**
     * GET /api/database/table/{tableName} - Rows of a table.
     */
    @GetMapping("/table/{tableName}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getTableData(@PathVariable String tableName) {
        Map<String, Object> data = databaseService.getTableData(tableName);
        return ResponseEntity.ok(ApiResponse.success("Table data retrieved.", data));
    }

    /**
     * GET /api/database/schema/{tableName} - Column structure and constraints.
     */
    @GetMapping("/schema/{tableName}")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getTableSchema(@PathVariable String tableName) {
        List<Map<String, Object>> schema = databaseService.getTableSchema(tableName);
        return ResponseEntity.ok(ApiResponse.success("Table schema retrieved.", schema));
    }

    /**
     * POST /api/database/query - Execute custom SELECT/SHOW query in web console.
     */
    @PostMapping("/query")
    public ResponseEntity<ApiResponse<Map<String, Object>>> executeQuery(@RequestBody Map<String, String> body) {
        String query = body.get("query");
        if (query == null || query.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Query cannot be empty."));
        }
        Map<String, Object> result = databaseService.executeQuery(query);
        return ResponseEntity.ok(ApiResponse.success("Query executed successfully.", result));
    }

    /**
     * POST /api/database/seed-demo - Populate sample data for instant demonstration.
     */
    @PostMapping("/seed-demo")
    public ResponseEntity<ApiResponse<Map<String, Object>>> seedDemo(HttpSession session) {
        Long userId = null;
        if (session != null && session.getAttribute(AuthInterceptor.SESSION_USER_ID) != null) {
            userId = (Long) session.getAttribute(AuthInterceptor.SESSION_USER_ID);
        }
        Map<String, Object> result = databaseService.seedVivaDemoData(userId);
        return ResponseEntity.ok(ApiResponse.success("Demo data seeded successfully.", result));
    }
}
