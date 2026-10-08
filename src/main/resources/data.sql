-- ===================================================================
-- SMART EXPENSE TRACKER - INITIAL CATEGORIES SEED DATA
-- ===================================================================

-- Income Categories
INSERT INTO categories (name, type) SELECT 'Salary', 'INCOME' WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Salary' AND type = 'INCOME');
INSERT INTO categories (name, type) SELECT 'Freelance', 'INCOME' WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Freelance' AND type = 'INCOME');
INSERT INTO categories (name, type) SELECT 'Business', 'INCOME' WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Business' AND type = 'INCOME');
INSERT INTO categories (name, type) SELECT 'Other Income', 'INCOME' WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Other Income' AND type = 'INCOME');

-- Expense Categories
INSERT INTO categories (name, type) SELECT 'Food', 'EXPENSE' WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Food' AND type = 'EXPENSE');
INSERT INTO categories (name, type) SELECT 'Transport', 'EXPENSE' WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Transport' AND type = 'EXPENSE');
INSERT INTO categories (name, type) SELECT 'Education', 'EXPENSE' WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Education' AND type = 'EXPENSE');
INSERT INTO categories (name, type) SELECT 'Shopping', 'EXPENSE' WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Shopping' AND type = 'EXPENSE');
INSERT INTO categories (name, type) SELECT 'Bills', 'EXPENSE' WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Bills' AND type = 'EXPENSE');
INSERT INTO categories (name, type) SELECT 'Entertainment', 'EXPENSE' WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Entertainment' AND type = 'EXPENSE');
INSERT INTO categories (name, type) SELECT 'Healthcare', 'EXPENSE' WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Healthcare' AND type = 'EXPENSE');
INSERT INTO categories (name, type) SELECT 'Other', 'EXPENSE' WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Other' AND type = 'EXPENSE');
