package com.example.smartexpensetracker.config;

import com.example.smartexpensetracker.entity.Category;
import com.example.smartexpensetracker.entity.TransactionType;
import com.example.smartexpensetracker.repository.CategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Initializes predefined Income and Expense categories on application startup.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final CategoryRepository categoryRepository;

    public DataInitializer(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void run(String... args) {
        seedCategories();
    }

    private void seedCategories() {
        List<Category> defaultCategories = List.of(
                // Income Categories
                new Category("Salary", TransactionType.INCOME),
                new Category("Freelance", TransactionType.INCOME),
                new Category("Business", TransactionType.INCOME),
                new Category("Other Income", TransactionType.INCOME),

                // Expense Categories
                new Category("Food", TransactionType.EXPENSE),
                new Category("Transport", TransactionType.EXPENSE),
                new Category("Education", TransactionType.EXPENSE),
                new Category("Shopping", TransactionType.EXPENSE),
                new Category("Bills", TransactionType.EXPENSE),
                new Category("Entertainment", TransactionType.EXPENSE),
                new Category("Healthcare", TransactionType.EXPENSE),
                new Category("Other", TransactionType.EXPENSE)
        );

        for (Category category : defaultCategories) {
            if (!categoryRepository.existsByNameAndType(category.getName(), category.getType())) {
                categoryRepository.save(category);
                logger.info("Seeded category: {} ({})", category.getName(), category.getType());
            }
        }
    }
}
