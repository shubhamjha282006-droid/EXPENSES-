# ACADEMIC PROJECT REPORT: SMART EXPENSE TRACKER

**Course:** B.E. / B.Tech Computer Engineering / Information Technology  
**Subject:** Advanced Java / Web Application Development / Software Engineering  
**Project Title:** Smart Expense Tracker (Personal Financial Management System)  
**Technology Stack:** Java 17+, Spring Boot 3, Spring Data JPA, Hibernate, MySQL, HTML5, CSS3, Bootstrap 5, JavaScript (Fetch API), Chart.js  

---

## 1. PROJECT INTRODUCTION

Managing day-to-day personal finances has become a vital requirement for individuals, students, and working professionals. In the absence of an organized system, users often struggle to monitor discretionary spending, evaluate cash flow, and maintain savings.

The **Smart Expense Tracker** is a full-stack web application developed to provide individuals with an intuitive, reliable, and secure platform to record their financial transactions (both earnings and expenditures), categorize items, monitor real-time net balances, and gain actionable visual insights into their spending habits through interactive graphical dashboards.

---

## 2. PROBLEM STATEMENT

Manual bookkeeping (e.g., paper diaries, mental estimations, or cumbersome spreadsheets) suffers from multiple shortcomings:
1. **Human Error:** Manual addition and balance calculations are prone to mathematical mistakes.
2. **Lack of Security:** Paper or unprotected files expose confidential financial figures to unauthorized persons.
3. **No Dynamic Insights:** Spreadsheets lack automated visual categorization, monthly trends, and instant KPI calculation.
4. **Data Fragmentation:** Income and expense records are frequently lost or unorganized over long timeframes.

Therefore, there is an urgent need for an automated, centralized, web-based software application that guarantees data privacy, prevents cross-user access, automates mathematical calculations, and delivers instant graphical summaries.

---

## 3. PROJECT OBJECTIVES

The core objectives of the Smart Expense Tracker project are:
- **User Authentication & Privacy:** Provide secure registration and login using industry-standard **BCrypt** password hashing, session management, and strict data isolation so each user only sees their own transactions.
- **Transaction Lifecycle Management:** Offer seamless CRUD (Create, Read, Update, Delete) capabilities for income and expense transactions with strict field validations.
- **Automated Balance Computation:** Automatically evaluate `Net Balance = Total Income - Total Expenses` and refresh metrics dynamically upon data modification.
- **Visual Analytics:** Deliver real-time graphical charts (Doughnut charts for expense category distribution, Grouped Bar charts for monthly Income vs. Expense comparisons) using Chart.js.
- **Search and Multi-Criteria Filtering:** Allow users to filter transactions by date range, transaction type, category, or search keywords in descriptions.
- **Academic Elegance:** Maintain clean, layered Java architectural principles (Controller → Service → Repository → MySQL) without unnecessary over-engineering.

---

## 4. SYSTEM FEATURES & MODULES

### 4.1 Module Breakdown

| Module Name | Responsibilities | Key Java Classes |
| :--- | :--- | :--- |
| **Authentication Module** | User signup, credential verification, BCrypt password hashing, session binding, logout. | `AuthController`, `AuthService`, `User`, `UserRepository`, `SecurityConfig` |
| **Security & Interceptor Module** | Intercepting unauthenticated requests, ensuring active sessions, preventing data leakage. | `AuthInterceptor`, `WebMvcConfig` |
| **Transaction Module** | Recording income/expenses, validation, updating records, deletion, filtering. | `TransactionController`, `TransactionService`, `Transaction`, `TransactionRepository` |
| **Category Module** | Providing predefined categories, dynamically querying categories by type (`INCOME`/`EXPENSE`). | `CategoryController`, `CategoryService`, `Category`, `CategoryRepository`, `DataInitializer` |
| **Dashboard & Analytics Module** | Computing totals, evaluating net balance, aggregating category percentages, generating monthly trends. | `DashboardController`, `DashboardService`, `Chart.js` Integration |
| **Error Handling Module** | Centralized handling of validation, authentication, and database exceptions. | `GlobalExceptionHandler`, `ErrorResponse` |

---

## 5. REQUIREMENTS SPECIFICATION

### 5.1 Functional Requirements (FR)
- **FR-1:** The system shall allow new users to register with a unique username, unique email, and password.
- **FR-2:** The system shall hash user passwords using BCrypt prior to storing them in the MySQL database.
- **FR-3:** The system shall allow registered users to authenticate using either their username or email along with their password.
- **FR-4:** The system shall maintain an HTTP session and invalidate it upon logout or session timeout.
- **FR-5:** The system shall allow users to record income transactions with amount, category, date, and description.
- **FR-6:** The system shall allow users to record expense transactions with amount, category, date, and description.
- **FR-7:** The system shall validate that transaction amounts are strictly positive ($> 0$).
- **FR-8:** The system shall allow users to edit and delete only their own transactions.
- **FR-9:** The system shall compute Net Balance dynamically: $\text{Net Balance} = \sum \text{Income} - \sum \text{Expense}$.
- **FR-10:** The system shall render an Expense by Category chart and a 6-month historical Income vs Expense chart.
- **FR-11:** The system shall allow filtering by Type, Category, Date range, and keyword search.

### 5.2 Non-Functional Requirements (NFR)
- **Security:** Passwords are never stored in plain text. SQL injection is mitigated through Hibernate parameterized queries. Cross-user data leakage is strictly blocked.
- **Performance:** Database indexing on foreign keys and date columns ensures rapid query execution ($< 100\text{ ms}$).
- **Usability:** Responsive user interface utilizing Bootstrap 5 that adapts seamlessly to desktop, tablet, and mobile browsers.
- **Maintainability:** Layered architecture adhering to Single Responsibility and Separation of Concerns principles.

---

## 6. SYSTEM ARCHITECTURE

The application follows the classic **Layered Enterprise Architecture**:

```
+-------------------------------------------------------------+
|                     PRESENTATION LAYER                      |
|       HTML5 / CSS3 / Bootstrap 5 / JavaScript / Chart.js    |
+------------------------------+------------------------------+
                               | HTTP / JSON REST APIs
+------------------------------v------------------------------+
|                     CONTROLLER LAYER                        |
|  AuthController | TransactionController | DashboardController|
+------------------------------+------------------------------+
                               | Java Method Calls & DTOs
+------------------------------v------------------------------+
|                       SERVICE LAYER                         |
|  AuthService | TransactionService | DashboardService        |
|  - Business Logic & Calculations (Net Balance)              |
|  - BCrypt Verification & Ownership Enforcement              |
+------------------------------+------------------------------+
                               | Spring Data JPA / Hibernate
+------------------------------v------------------------------+
|                     REPOSITORY LAYER                        |
|  UserRepository | TransactionRepository | CategoryRepository|
+------------------------------+------------------------------+
                               | JDBC Driver (mysql-connector-j)
+------------------------------v------------------------------+
|                      DATABASE LAYER                         |
|                 MySQL 8.0 Relational Database                |
+-------------------------------------------------------------+
```

---

## 7. DATABASE SCHEMA & ER DIAGRAM

### 7.1 Entity Relationship (ER) Description

```
       1             N
[USERS] <-------------- [TRANSACTIONS]
                          |
                          | N
                          |
                          v 1
                    [CATEGORIES]
```

- **User to Transaction (1 : N):** One user can have zero, one, or many transactions. Each transaction belongs strictly to exactly one user.
- **Category to Transaction (1 : N):** One category (e.g., "Food", "Salary") can classify multiple transactions. Each transaction is linked to exactly one category.

### 7.2 Tables Description

#### `users` Table
| Column Name | Data Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique user identifier |
| `username` | VARCHAR(50) | UNIQUE, NOT NULL | Unique login handle |
| `email` | VARCHAR(100) | UNIQUE, NOT NULL | User contact email |
| `password` | VARCHAR(255) | NOT NULL | BCrypt hashed password |
| `created_at` | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Account creation date |

#### `categories` Table
| Column Name | Data Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique category identifier |
| `name` | VARCHAR(50) | NOT NULL | Category title (e.g., "Salary", "Food") |
| `type` | VARCHAR(20) | NOT NULL | Category classification (`INCOME` / `EXPENSE`) |

#### `transactions` Table
| Column Name | Data Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique transaction identifier |
| `user_id` | BIGINT | FOREIGN KEY (`users.id`), NOT NULL | Owner of the transaction |
| `category_id` | BIGINT | FOREIGN KEY (`categories.id`), NOT NULL | Associated category |
| `amount` | DECIMAL(10,2)| NOT NULL, CHECK ($> 0$) | Monetary amount |
| `type` | VARCHAR(20) | NOT NULL | `INCOME` or `EXPENSE` |
| `description`| VARCHAR(255) | NULLABLE | Notes or narrative |
| `transaction_date` | DATE | NOT NULL | Effective date of the transaction |
| `created_at` | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Record insertion timestamp |

---

## 8. REST API DOCUMENTATION

| Method | Endpoint | Request Body | Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/auth/register` | `RegisterRequest` | Registers user with BCrypt hash |
| `POST` | `/api/auth/login` | `LoginRequest` | Validates credentials & creates session |
| `POST` | `/api/auth/logout` | None | Destroys current HTTP session |
| `GET` | `/api/auth/me` | None | Returns active user profile |
| `GET` | `/api/categories` | Query: `?type=EXPENSE` | Retrieves predefined categories |
| `GET` | `/api/transactions` | Query params (optional) | Retrieves user transactions with filters |
| `POST` | `/api/transactions` | `TransactionRequest` | Records a new income/expense |
| `GET` | `/api/transactions/{id}`| None | Fetches single transaction details |
| `PUT` | `/api/transactions/{id}`| `TransactionRequest` | Updates an existing transaction |
| `DELETE`| `/api/transactions/{id}`| None | Deletes an owned transaction |
| `GET` | `/api/dashboard/summary`| None | Total Income, Expense, Net Balance, Recent txs |
| `GET` | `/api/dashboard/category-expenses` | None | Categorized expenses for Doughnut Chart |
| `GET` | `/api/dashboard/monthly-summary` | None | 6-month historical totals for Bar Chart |

---

## 9. TESTING PLAN

| Test ID | Test Scenario | Input Data | Expected Result | Status |
| :--- | :--- | :--- | :--- | :--- |
| **TC-01** | User Registration with valid credentials | Username: `john_doe`, Email: `john@example.com`, Password: `password123` | User registered, HTTP 201 Created | Pass |
| **TC-02** | Registration with duplicate username/email | Existing username or email | HTTP 400 Bad Request with error description | Pass |
| **TC-03** | Registration with mismatching passwords | Passwords do not match | HTTP 400 Bad Request: "Passwords do not match" | Pass |
| **TC-04** | Login with valid credentials | Valid username & password | HTTP 200 OK, session established | Pass |
| **TC-05** | Login with invalid password | Incorrect password | HTTP 401 Unauthorized | Pass |
| **TC-06** | Add Income transaction | Amount: ₹50,000, Type: INCOME, Category: "Salary" | Transaction recorded, Net Balance increases | Pass |
| **TC-07** | Add Expense transaction | Amount: ₹3,500, Type: EXPENSE, Category: "Food" | Transaction recorded, Net Balance decreases | Pass |
| **TC-08** | Transaction with invalid amount ($\le 0$) | Amount: 0 or negative | HTTP 400 Validation Error | Pass |
| **TC-09** | User Data Isolation | User B requests User A's transaction ID | HTTP 404 / 401: Access Denied | Pass |
| **TC-10** | Filter Transactions by Date & Type | Type: EXPENSE, Date: 2026-10-01 to 2026-10-08 | Returns only matching records | Pass |
| **TC-11** | Dashboard Charts Update | Adding a new expense of ₹2,000 in "Transport" | Chart re-renders with new slice and updated balance | Pass |

---

## 10. FUTURE ENHANCEMENTS

1. **Monthly Budget Alerts:** Enable users to set monthly spending caps per category and receive notifications when 80% or 100% of the limit is breached.
2. **Export to PDF & Excel (CSV):** Allow users to export statement reports for taxation and personal archiving.
3. **Receipt Image Upload & OCR:** Use Optical Character Recognition (OCR) to automatically extract transaction amount and date from scanned receipts.
4. **Recurring Subscriptions & Reminders:** Automatically log monthly utility bills, rent, and subscriptions on specific calendar days.
5. **Multi-Currency Support:** Support currency conversion rates using an external financial exchange rate API.

---

## 11. CONCLUSION

The **Smart Expense Tracker** successfully addresses the challenge of personal financial management by replacing error-prone manual bookkeeping with a secure, automated, and visually informative web application. Developed using industry-standard enterprise Java technologies (Spring Boot, Spring Data JPA, Hibernate, MySQL) alongside modern web standards (Bootstrap 5, Chart.js), the system exhibits robust software engineering practices, clear data separation, and intuitive design. It serves as an exemplary academic project that is easy to demonstrate, explain, and defend during technical vivas.

---

## 12. COMPREHENSIVE VIVA QUESTIONS AND ANSWERS

### Q1: What is the architecture of this application?
**Answer:** The application follows the standard three-tier / layered enterprise architecture:
1. **Presentation Tier (Frontend):** HTML5, CSS3, Bootstrap 5, and JavaScript utilizing the Fetch API for asynchronous communication and Chart.js for data visualization.
2. **Business Tier (Backend):** Built with Spring Boot (Java 17+), structured into:
   - **Controllers:** Handle HTTP requests, map endpoints, and serialize/deserialize JSON.
   - **Services:** Execute core business rules, calculations (e.g., Net Balance), and validations.
   - **Repositories:** Perform database interactions using Spring Data JPA.
3. **Data Tier (Database):** MySQL 8.0 relational database storing users, categories, and transactions with foreign key relationships.

### Q2: Why did you choose Spring Boot instead of standard Spring MVC or Servlets?
**Answer:** 
- Spring Boot provides **opinionated auto-configuration**, eliminating complex XML configuration.
- It comes with an **embedded Tomcat server**, so the project packages into a standalone executable JAR without needing external server installations.
- It provides **Spring Data JPA**, which reduces boilerplate JDBC data-access code to simple interface declarations.
- It integrates built-in **dependency management** via starters (e.g., `spring-boot-starter-web`, `spring-boot-starter-data-jpa`).

### Q3: How do you secure user passwords?
**Answer:** Passwords are never stored in plain text. We utilize the **BCrypt cryptographic hashing algorithm** via `BCryptPasswordEncoder`. BCrypt automatically incorporates a randomly generated salt and an adaptive work factor (cost parameter 10). When a user logs in, the `passwordEncoder.matches(rawPassword, encodedPassword)` method verifies the entered credentials against the stored hash without ever reversing the hash.

### Q4: How is User Data Isolation enforced?
**Answer:** 
Data isolation is enforced at the service and database layer:
1. When a user logs in, their authenticated `userId` is stored in the `HttpSession`.
2. For all transaction queries, deletions, and updates, the repository method explicitly requires both the `id` and the `userId`:
   ```java
   Optional<Transaction> findByIdAndUserId(Long id, Long userId);
   ```
3. Therefore, even if a malicious user alters the transaction ID in the URL or payload to another user's ID, the query returns empty, throwing a `ResourceNotFoundException`. A user can never view or manipulate another user's financial records.

### Q5: How is the Net Balance calculated?
**Answer:** 
In `DashboardService.java`:
$$\text{Net Balance} = \text{Total Income} - \text{Total Expenses}$$
The service invokes `TransactionRepository.sumAmountByUserIdAndType(userId, TransactionType.INCOME)` and `TransactionRepository.sumAmountByUserIdAndType(userId, TransactionType.EXPENSE)`. The difference is calculated using `BigDecimal.subtract()` to maintain monetary precision without floating-point rounding errors.

### Q6: Why did you use `BigDecimal` instead of `double` or `float` for monetary amounts?
**Answer:** In Java, primitive `double` and `float` use binary IEEE 754 floating-point representations, which cannot represent numbers like $0.1$ or $0.01$ exactly, causing rounding errors in financial computations. `BigDecimal` provides exact decimal arithmetic with arbitrary precision and configurable rounding modes, making it the mandatory standard for financial applications.

### Q7: What are DTOs and why are they used here?
**Answer:** DTO stands for **Data Transfer Object**. They decouple the internal database entities from external API request/response contracts:
1. **Security:** Prevents exposing sensitive fields (such as user password hashes) in API JSON responses.
2. **Encapsulation:** Protects internal database table structures from client visibility.
3. **Validation:** Allows annotating request payloads with Bean Validation constraints (`@NotBlank`, `@DecimalMin`, `@Email`) before entities are touched.

### Q8: How does the application handle exceptions centrally?
**Answer:** We implemented `@RestControllerAdvice` in `GlobalExceptionHandler.java`. Any exception thrown by controllers or services (such as `MethodArgumentNotValidException`, `ResourceNotFoundException`, `UnauthorizedException`, or `BadRequestException`) is intercepted and translated into a clean, unified `ErrorResponse` JSON with appropriate HTTP status codes (400, 401, 404, 500) rather than exposing stack traces to the client.

### Q9: How do the charts in the frontend stay up to date?
**Answer:** The frontend uses **Chart.js** coupled with JavaScript's Fetch API. Whenever a transaction is created, updated, or deleted, the frontend invokes `DashboardModule.refreshDashboard()`, which makes asynchronous HTTP GET requests to `/api/dashboard/summary`, `/api/dashboard/category-expenses`, and `/api/dashboard/monthly-summary`. Upon receiving updated JSON data, the Chart.js chart instances are updated and re-rendered without requiring a full browser page reload.
