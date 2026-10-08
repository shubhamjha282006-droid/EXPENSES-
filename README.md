# SMART EXPENSE TRACKER

**A secure, modern, beginner-friendly Personal Financial Management Web Application.**

Designed for academic demonstration, college viva examinations, and practical software engineering evaluation.

---

## 🌟 Technology Stack

- **Backend:** Java 17+, Spring Boot 3.3.4 (Spring MVC / REST API, Spring Data JPA, Hibernate, Bean Validation)
- **Security:** BCrypt cryptographic hashing for passwords, session-based authentication & data isolation
- **Database:** MySQL 8.0 (InnoDB, UTF-8)
- **Build Tool:** Maven
- **Frontend:** HTML5, CSS3, Bootstrap 5.3, JavaScript (Fetch API, ES6+), Chart.js 4.4
- **Database Explorer:** Built-in web-based MySQL Table Inspector, Schema Viewer, and SQL Console inside the browser!

---

## 📁 Project Architecture & Package Structure

```
smart-expense-tracker/
├── pom.xml                                   # Maven dependencies and build plugins
├── run.bat                                   # One-click Windows startup script
├── README.md                                 # Complete setup and testing guide
├── PROJECT_REPORT.md                         # Full academic report and Viva Q&A
└── src/
    ├── main/
    │   ├── java/
    │   │   └── com/
    │   │       └── example/
    │   │           └── smartexpensetracker/
    │   │               ├── SmartExpenseTrackerApplication.java  # Main Spring Boot entry point
    │   │               ├── config/
    │   │               │   ├── SecurityConfig.java              # BCrypt bean definition
    │   │               │   ├── WebMvcConfig.java                # MVC interceptor configuration
    │   │               │   ├── AuthInterceptor.java             # Session guard interceptor
    │   │               │   └── DataInitializer.java            # Default category seeder
    │   │               ├── controller/
    │   │               │   ├── AuthController.java              # Registration, login, logout APIs
    │   │               │   ├── TransactionController.java       # CRUD & filtering REST APIs
    │   │               │   ├── CategoryController.java          # Category listing APIs
    │   │               │   └── DashboardController.java         # Financial KPI & chart APIs
    │   │               ├── dto/
    │   │               │   ├── RegisterRequest.java             # Registration payload
    │   │               │   ├── LoginRequest.java                # Login payload
    │   │               │   ├── UserResponse.java                # Safe user DTO (no password)
    │   │               │   ├── TransactionRequest.java          # Create/Update tx payload
    │   │               │   ├── TransactionResponse.java         # Tx response payload
    │   │               │   ├── CategoryResponse.java            # Category DTO
    │   │               │   ├── DashboardSummaryResponse.java    # KPI & recent tx summary
    │   │               │   ├── CategoryExpenseResponse.java     # Doughnut chart metrics
    │   │               │   ├── MonthlySummaryResponse.java      # Bar chart monthly metrics
    │   │               │   └── ApiResponse.java                 # Standard generic JSON wrapper
    │   │               ├── entity/
    │   │               │   ├── User.java                        # User entity mapping (users table)
    │   │               │   ├── Category.java                    # Category entity mapping (categories table)
    │   │               │   ├── Transaction.java                 # Transaction entity mapping (transactions table)
    │   │               │   └── TransactionType.java             # Enum: INCOME, EXPENSE
    │   │               ├── exception/
    │   │               │   ├── ResourceNotFoundException.java   # 404 handler
    │   │               │   ├── UnauthorizedException.java       # 401 handler
    │   │               │   ├── BadRequestException.java         # 400 handler
    │   │               │   ├── ErrorResponse.java               # Standard JSON error object
    │   │               │   └── GlobalExceptionHandler.java      # Centralized @RestControllerAdvice
    │   │               ├── repository/
    │   │               │   ├── UserRepository.java              # Spring Data JPA User repository
    │   │               │   ├── CategoryRepository.java          # Category repository
    │   │               │   └── TransactionRepository.java       # Custom JPQL queries & aggregations
    │   │               └── service/
    │   │                   ├── AuthService.java                 # Auth logic & BCrypt hashing
    │   │                   ├── CategoryService.java             # Category business logic
    │   │                   ├── TransactionService.java          # Transaction validation & isolation
    │   │                   └── DashboardService.java            # Net balance & Chart metrics
    │   └── resources/
    │       ├── application.properties                           # MySQL and server properties
    │       ├── schema.sql                                       # Explicit MySQL DDL schema
    │       ├── data.sql                                         # Initial category seed data
    │       └── static/                                          # Frontend assets served by Spring
    │           ├── index.html                                   # Single-page dashboard & UI
    │           ├── css/
    │           │   └── style.css                                # Modern responsive styling
    │           └── js/
    │               └── app.js                                   # Fetch API & Chart.js logic
    └── test/
        └── java/
            └── com/example/smartexpensetracker/
                └── SmartExpenseTrackerApplicationTests.java    # Unit tests (BCrypt, Balance, DTOs)
```

---

## 🚀 Setup & Execution Guide

### Prerequisites
1. **Java JDK 17 or later** installed.
2. **MySQL Server 8.0** installed and running.
3. **IDE (VS Code, IntelliJ IDEA, Eclipse)** or **Maven command line**.

---

### Step 1: Create the MySQL Database
Open your MySQL Command Line Client or MySQL Workbench:
```sql
CREATE DATABASE IF NOT EXISTS smart_expense_tracker;
```

---

### Step 2: Configure Database Credentials
Open `src/main/resources/application.properties` and verify your MySQL password:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/smart_expense_tracker?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD
```
*(Replace `YOUR_MYSQL_PASSWORD` with your actual local MySQL root password, e.g., `root`, `admin`, or `password123`)*

Hibernate is set to `spring.jpa.hibernate.ddl-auto=update`, which means all tables (`users`, `categories`, `transactions`) and foreign keys will be created automatically on the first run! Predefined categories will also be automatically seeded on startup by `DataInitializer.java`.

---

### Step 3: Run the Application

#### Option A: Using the convenient Windows script
Simply run the included batch script in PowerShell or Command Prompt:
```cmd
.\run.bat
```

#### Option B: Using Maven CLI
```cmd
mvn spring-boot:run
```

#### Option C: In IntelliJ IDEA / VS Code / Eclipse
- Open the `smart-expense-tracker` folder in your IDE.
- Locate `src/main/java/com/example/smartexpensetracker/SmartExpenseTrackerApplication.java`.
- Right-click and choose **Run 'SmartExpenseTrackerApplication.main()'**.

---

### Step 4: Open the Application
Open your web browser and navigate to:
```
http://localhost:8081
```

---

## 🧪 Demonstration & Testing Walkthrough

Follow these steps for a complete project demonstration:

### 1. Register a New Account
- Click on the **Register** tab.
- Enter:
  - Username: `rahul_sharma`
  - Email: `rahul@example.com`
  - Password: `Password@123`
  - Confirm Password: `Password@123`
- Click **Create Account**. A success toast confirms registration and automatically opens the login tab.

### 2. Login
- Enter `rahul_sharma` (or `rahul@example.com`) and `Password@123`.
- Click **Sign In**.
- You are welcomed with a personalized toast and navigated directly to your Financial Dashboard.

### 3. Record Income
- Click **+ Add Income** on the dashboard.
- Select Category: **Salary**.
- Enter Amount: `50000`.
- Select Date: Today's date.
- Description: `Monthly company salary`.
- Click **Save Transaction**.
- Observe the **Total Income** stat card update to `₹50,000.00` and **Net Balance** update to `₹50,000.00`.

### 4. Record Expenses
- Click **- Add Expense** and record:
  - Category: **Food**, Amount: `5000`, Description: `Monthly groceries & dining`
  - Category: **Transport**, Amount: `2000`, Description: `Metro card recharge & fuel`
  - Category: **Education**, Amount: `3000`, Description: `Books and online course`
- Observe the **Total Expenses** stat card update to `₹10,000.00`.
- Observe the **Net Balance** card update to:
  $$\text{Net Balance} = 50000 - 10000 = \text{₹40,000.00}$$

### 5. Check Interactive Charts
- **Expense by Category Chart (Doughnut):**
  - Displays slices for Food (₹5,000 = 50%), Transport (₹2,000 = 20%), and Education (₹3,000 = 30%).
- **Monthly Trend Chart (Bar):**
  - Displays the current month comparing Income (₹50,000 in green) against Expenses (₹10,000 in red).

### 6. Filter & Search Transactions
- Switch to the **Transactions** view using the navbar.
- Filter by Type: select `Expense` and click the filter button → only expenses appear.
- Filter by Category: select `Food` → only the grocery transaction appears.
- Search keyword: type `Metro` in the search box → only the Transport transaction appears.
- Click **Reset** (circular arrow) to restore the full list.

### 7. Edit and Delete
- Click the pencil icon on any transaction to edit the amount or description.
- Save and verify the updated amount reflects immediately in the table and dashboard.
- Click the trash icon to open the confirmation modal and delete the transaction.
- Observe that the transaction is deleted and the dashboard balance refreshes automatically.

### 8. User Data Isolation Verification
- Click **Logout**.
- Register a second user (e.g., `priya_patel`).
- Log in as `priya_patel`.
- Observe that Priya's dashboard shows `₹0.00` with no access to Rahul's financial records!

---

## 📡 REST API Reference

| HTTP Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/api/auth/register` | Register new user with BCrypt hash |
| `POST` | `/api/auth/login` | Authenticate user and initialize session |
| `POST` | `/api/auth/logout` | Invalidate session |
| `GET` | `/api/auth/me` | Fetch active authenticated user profile |
| `GET` | `/api/categories` | List categories (optional `?type=INCOME` or `?type=EXPENSE`) |
| `GET` | `/api/transactions` | Query transactions with filters (`type`, `categoryId`, `startDate`, `endDate`, `keyword`) |
| `POST` | `/api/transactions` | Create new transaction |
| `GET` | `/api/transactions/{id}` | Get transaction by ID |
| `PUT` | `/api/transactions/{id}` | Update existing transaction |
| `DELETE` | `/api/transactions/{id}` | Delete transaction |
| `GET` | `/api/dashboard/summary` | Dashboard KPIs (Income, Expense, Net Balance, Recent 5 txs) |
| `GET` | `/api/dashboard/category-expenses` | Category breakdown for Doughnut Chart |
| `GET` | `/api/dashboard/monthly-summary` | 6-month historical totals for Bar Chart |

---

## 🎓 Academic Viva Highlights

1. **Password Security:** BCrypt hash with 10 rounds of salt generation. Passwords are never stored or logged in plain text.
2. **Precision Arithmetic:** Monetary values use `BigDecimal` to prevent IEEE 754 binary floating-point inaccuracies.
3. **Layered Design:** Strict separation between Controller (HTTP), Service (Business Logic), Repository (Spring Data JPA), and Entity (Hibernate ORM).
4. **Data Isolation:** All database modifications and queries explicitly check `WHERE user_id = :authenticatedUserId`.
5. **Exception Handling:** Centralized `@RestControllerAdvice` maps exceptions to clean `ErrorResponse` objects with standard HTTP status codes (200, 201, 400, 401, 404, 500).
#   E X P E N S E S -  
 "# EXPENSES-" 
"# EXPENSES-" 
