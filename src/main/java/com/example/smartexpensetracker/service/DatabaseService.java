package com.example.smartexpensetracker.service;

import com.example.smartexpensetracker.entity.Category;
import com.example.smartexpensetracker.entity.Transaction;
import com.example.smartexpensetracker.entity.TransactionType;
import com.example.smartexpensetracker.entity.User;
import com.example.smartexpensetracker.exception.BadRequestException;
import com.example.smartexpensetracker.exception.ResourceNotFoundException;
import com.example.smartexpensetracker.repository.CategoryRepository;
import com.example.smartexpensetracker.repository.TransactionRepository;
import com.example.smartexpensetracker.repository.UserRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

/**
 * Service providing web-based database management, table inspection,
 * query execution, and demo data seeding for viva demonstrations.
 */
@Service
public class DatabaseService {

    private final JdbcTemplate jdbcTemplate;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;

    private static final Set<String> ALLOWED_TABLES = Set.of("users", "categories", "transactions");

    public DatabaseService(JdbcTemplate jdbcTemplate,
                           UserRepository userRepository,
                           CategoryRepository categoryRepository,
                           TransactionRepository transactionRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.transactionRepository = transactionRepository;
    }

    /**
     * Retrieve high-level database health and metadata.
     */
    public Map<String, Object> getDatabaseInfo() {
        Map<String, Object> info = new LinkedHashMap<>();
        try {
            String dbVersion = jdbcTemplate.queryForObject("SELECT VERSION()", String.class);
            info.put("engine", "MySQL 8.0 (InnoDB)");
            info.put("version", dbVersion);
            info.put("databaseName", "smart_expense_tracker");
            info.put("status", "ONLINE");

            Long userCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users", Long.class);
            Long categoryCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM categories", Long.class);
            Long transactionCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM transactions", Long.class);

            info.put("userCount", userCount != null ? userCount : 0);
            info.put("categoryCount", categoryCount != null ? categoryCount : 0);
            info.put("transactionCount", transactionCount != null ? transactionCount : 0);
            info.put("totalTables", 3);
        } catch (Exception e) {
            info.put("status", "ERROR: " + e.getMessage());
        }
        return info;
    }

    /**
     * Inspect rows from a selected database table.
     */
    public Map<String, Object> getTableData(String tableName) {
        String cleanTable = tableName.trim().toLowerCase();
        if (!ALLOWED_TABLES.contains(cleanTable)) {
            throw new BadRequestException("Table '" + tableName + "' is not accessible.");
        }

        String sql = "SELECT * FROM " + cleanTable + " ORDER BY id ASC LIMIT 100";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);

        List<String> columns = new ArrayList<>();
        if (!rows.isEmpty()) {
            columns.addAll(rows.get(0).keySet());
        } else {
            // Retrieve column names even if table is empty
            List<Map<String, Object>> descRows = jdbcTemplate.queryForList("DESCRIBE " + cleanTable);
            for (Map<String, Object> row : descRows) {
                columns.add((String) row.get("Field"));
            }
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("table", cleanTable);
        result.put("columns", columns);
        result.put("rows", rows);
        result.put("rowCount", rows.size());
        return result;
    }

    /**
     * Retrieve column definitions and constraints for a table.
     */
    public List<Map<String, Object>> getTableSchema(String tableName) {
        String cleanTable = tableName.trim().toLowerCase();
        if (!ALLOWED_TABLES.contains(cleanTable)) {
            throw new BadRequestException("Table '" + tableName + "' is not accessible.");
        }
        return jdbcTemplate.queryForList("DESCRIBE " + cleanTable);
    }

    /**
     * Execute a user-provided SELECT or SHOW query in the web console.
     */
    public Map<String, Object> executeQuery(String query) {
        String trimmed = query.trim();
        String upper = trimmed.toUpperCase();

        if (!upper.startsWith("SELECT") && !upper.startsWith("SHOW") && !upper.startsWith("DESCRIBE")) {
            throw new BadRequestException("Only SELECT, SHOW, and DESCRIBE queries are permitted in the web console.");
        }

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(trimmed);
        List<String> columns = new ArrayList<>();
        if (!rows.isEmpty()) {
            columns.addAll(rows.get(0).keySet());
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("columns", columns);
        result.put("rows", rows);
        result.put("rowCount", rows.size());
        return result;
    }

    /**
     * Seeds rich, realistic demonstration data for viva evaluation.
     */
    @Transactional
    public Map<String, Object> seedVivaDemoData(Long targetUserId) {
        User user = null;
        if (targetUserId != null) {
            user = userRepository.findById(targetUserId).orElse(null);
        }

        if (user == null) {
            // Pick first user, or create demo student
            List<User> users = userRepository.findAll();
            if (!users.isEmpty()) {
                user = users.get(0);
            } else {
                throw new BadRequestException("Please register or log in first before seeding demo data.");
            }
        }

        Map<String, Category> catMap = new HashMap<>();
        for (Category c : categoryRepository.findAll()) {
            catMap.put(c.getName(), c);
        }

        LocalDate today = LocalDate.now();
        List<Transaction> demoList = new ArrayList<>();

        // Helper lambda/method
        addDemoTx(demoList, user, catMap.get("Salary"), new BigDecimal("65000.00"), TransactionType.INCOME, "Monthly Corporate Salary", today.minusDays(25));
        addDemoTx(demoList, user, catMap.get("Freelance"), new BigDecimal("18500.00"), TransactionType.INCOME, "Full-Stack Web Dev Client Project", today.minusDays(18));
        addDemoTx(demoList, user, catMap.get("Salary"), new BigDecimal("65000.00"), TransactionType.INCOME, "Monthly Corporate Salary", today.minusDays(2));

        addDemoTx(demoList, user, catMap.get("Food"), new BigDecimal("4500.00"), TransactionType.EXPENSE, "Supermarket Grocery Shopping", today.minusDays(24));
        addDemoTx(demoList, user, catMap.get("Food"), new BigDecimal("2800.00"), TransactionType.EXPENSE, "Weekend Family Dinner", today.minusDays(15));
        addDemoTx(demoList, user, catMap.get("Transport"), new BigDecimal("2200.00"), TransactionType.EXPENSE, "Monthly Metro Pass & Fuel", today.minusDays(20));
        addDemoTx(demoList, user, catMap.get("Transport"), new BigDecimal("1100.00"), TransactionType.EXPENSE, "Cab Rides & Tolls", today.minusDays(10));
        addDemoTx(demoList, user, catMap.get("Bills"), new BigDecimal("3800.00"), TransactionType.EXPENSE, "Electricity & High-speed Internet Bill", today.minusDays(22));
        addDemoTx(demoList, user, catMap.get("Bills"), new BigDecimal("1500.00"), TransactionType.EXPENSE, "Mobile & Water Utility Bills", today.minusDays(8));
        addDemoTx(demoList, user, catMap.get("Education"), new BigDecimal("4999.00"), TransactionType.EXPENSE, "Spring Boot & Cloud Certification Course", today.minusDays(19));
        addDemoTx(demoList, user, catMap.get("Shopping"), new BigDecimal("5400.00"), TransactionType.EXPENSE, "Clothing and ergonomic desk accessories", today.minusDays(14));
        addDemoTx(demoList, user, catMap.get("Entertainment"), new BigDecimal("1999.00"), TransactionType.EXPENSE, "Movie tickets and OTT Subscriptions", today.minusDays(12));
        addDemoTx(demoList, user, catMap.get("Healthcare"), new BigDecimal("1850.00"), TransactionType.EXPENSE, "Health checkup and vitamins", today.minusDays(7));
        addDemoTx(demoList, user, catMap.get("Food"), new BigDecimal("1600.00"), TransactionType.EXPENSE, "Weekly Organic Veggies & Milk", today.minusDays(3));

        transactionRepository.saveAll(demoList);

        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("seededCount", demoList.size());
        resp.put("username", user.getUsername());
        resp.put("message", "Successfully seeded " + demoList.size() + " realistic transactions for " + user.getUsername() + "!");
        return resp;
    }

    private void addDemoTx(List<Transaction> list, User user, Category cat, BigDecimal amt, TransactionType type, String desc, LocalDate date) {
        if (cat != null) {
            Transaction t = new Transaction();
            t.setUser(user);
            t.setCategory(cat);
            t.setAmount(amt);
            t.setType(type);
            t.setDescription(desc);
            t.setTransactionDate(date);
            list.add(t);
        }
    }
}
