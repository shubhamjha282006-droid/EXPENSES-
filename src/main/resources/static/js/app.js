/**
 * SMART EXPENSE TRACKER - FRONTEND JAVASCRIPT
 * Handles Authentication, Transaction CRUD, Filtering, and Chart.js Analytics.
 */

// ================= GLOBAL STATE =================
let currentUser = null;
let categoryChartInstance = null;
let monthlyChartInstance = null;
let transactionModalInstance = null;
let deleteModalInstance = null;
let pendingDeleteId = null;

// ================= UTILITIES =================
const Utils = {
    formatCurrency(amount) {
        if (amount === null || amount === undefined) amount = 0;
        return new Intl.NumberFormat('en-IN', {
            style: 'currency',
            currency: 'INR',
            maximumFractionDigits: 2
        }).format(amount);
    },

    formatDate(dateStr) {
        if (!dateStr) return '';
        const d = new Date(dateStr + 'T00:00:00');
        return d.toLocaleDateString('en-IN', {
            day: '2-digit',
            month: 'short',
            year: 'numeric'
        });
    },

    getTodayDate() {
        return new Date().toISOString().split('T')[0];
    },

    showToast(message, type = 'info') {
        const container = document.getElementById('toastContainer');
        const toastId = 'toast-' + Date.now();
        const bgClass = type === 'success' ? 'text-bg-success' :
                        type === 'error' ? 'text-bg-danger' :
                        type === 'warning' ? 'text-bg-warning' : 'text-bg-primary';

        const toastEl = document.createElement('div');
        toastEl.className = `toast align-items-center ${bgClass} border-0 show mb-2`;
        toastEl.setAttribute('role', 'alert');
        toastEl.id = toastId;
        toastEl.innerHTML = `
            <div class="d-flex">
                <div class="toast-body fw-semibold">${message}</div>
                <button type="button" class="btn-close btn-close-white me-2 m-auto" onclick="document.getElementById('${toastId}').remove()"></button>
            </div>
        `;
        container.appendChild(toastEl);
        setTimeout(() => {
            const el = document.getElementById(toastId);
            if (el) el.remove();
        }, 4000);
    }
};

// ================= UI CONTROLLER =================
const UI = {
    init() {
        transactionModalInstance = new bootstrap.Modal(document.getElementById('transactionModal'));
        deleteModalInstance = new bootstrap.Modal(document.getElementById('deleteModal'));

        document.getElementById('confirmDeleteBtn').addEventListener('click', () => {
            if (pendingDeleteId) {
                TransactionModule.executeDelete(pendingDeleteId);
            }
        });

        // Initialize session check
        AuthModule.checkSession();
    },

    showAuthView() {
        document.getElementById('authView').classList.remove('d-none');
        document.getElementById('appContainer').classList.add('d-none');
        document.getElementById('authenticatedNav').classList.add('d-none');
        const unauth = document.getElementById('unauthenticatedNav');
        if (unauth) unauth.classList.remove('d-none');
        currentUser = null;
    },

    showAuthenticatedView(user) {
        currentUser = user;
        document.getElementById('authView').classList.add('d-none');
        document.getElementById('appContainer').classList.remove('d-none');
        document.getElementById('authenticatedNav').classList.remove('d-none');
        const unauth = document.getElementById('unauthenticatedNav');
        if (unauth) unauth.classList.add('d-none');

        document.getElementById('navUsername').textContent = user.username;
        document.getElementById('navEmail').textContent = user.email;

        // Populate category dropdown for filter
        TransactionModule.populateFilterCategories();

        this.showView('dashboard');
    },

    showView(viewName) {
        const dashboardView = document.getElementById('dashboardView');
        const transactionsView = document.getElementById('transactionsView');
        const databaseView = document.getElementById('databaseView');
        const authView = document.getElementById('authView');
        const appContainer = document.getElementById('appContainer');

        const navDashboardBtn = document.getElementById('navDashboardBtn');
        const navTransactionsBtn = document.getElementById('navTransactionsBtn');
        const navDatabaseBtn = document.getElementById('navDatabaseBtn');

        if (viewName === 'database') {
            authView.classList.add('d-none');
            appContainer.classList.remove('d-none');
            dashboardView.classList.add('d-none');
            transactionsView.classList.add('d-none');
            databaseView.classList.remove('d-none');

            if (navDatabaseBtn) {
                navDatabaseBtn.classList.add('active', 'btn-primary');
                navDatabaseBtn.classList.remove('btn-outline-secondary');
            }
            if (navDashboardBtn) {
                navDashboardBtn.classList.remove('active', 'btn-primary');
                navDashboardBtn.classList.add('btn-outline-secondary');
            }
            if (navTransactionsBtn) {
                navTransactionsBtn.classList.remove('active', 'btn-primary');
                navTransactionsBtn.classList.add('btn-outline-secondary');
            }

            DatabaseModule.refresh();
            return;
        }

        databaseView.classList.add('d-none');
        if (navDatabaseBtn) {
            navDatabaseBtn.classList.remove('active', 'btn-primary');
            navDatabaseBtn.classList.add('btn-outline-secondary');
        }

        if (!currentUser) {
            this.showAuthView();
            return;
        }

        if (viewName === 'dashboard') {
            dashboardView.classList.remove('d-none');
            transactionsView.classList.add('d-none');
            if (navDashboardBtn) {
                navDashboardBtn.classList.add('active', 'btn-primary');
                navDashboardBtn.classList.remove('btn-outline-secondary');
            }
            if (navTransactionsBtn) {
                navTransactionsBtn.classList.remove('active', 'btn-primary');
                navTransactionsBtn.classList.add('btn-outline-secondary');
            }
            DashboardModule.refreshDashboard();
        } else if (viewName === 'transactions') {
            dashboardView.classList.add('d-none');
            transactionsView.classList.remove('d-none');
            if (navTransactionsBtn) {
                navTransactionsBtn.classList.add('active', 'btn-primary');
                navTransactionsBtn.classList.remove('btn-outline-secondary');
            }
            if (navDashboardBtn) {
                navDashboardBtn.classList.remove('active', 'btn-primary');
                navDashboardBtn.classList.add('btn-outline-secondary');
            }
            TransactionModule.loadTransactions();
        }
    }
};

// ================= AUTHENTICATION MODULE =================
const AuthModule = {
    async checkSession() {
        try {
            const res = await fetch('/api/auth/me');
            if (res.ok) {
                const json = await res.json();
                UI.showAuthenticatedView(json.data);
            } else {
                UI.showAuthView();
            }
        } catch (err) {
            UI.showAuthView();
        }
    },

    async login(event) {
        event.preventDefault();
        const submitBtn = document.getElementById('loginSubmitBtn');
        submitBtn.disabled = true;

        const payload = {
            usernameOrEmail: document.getElementById('loginIdentifier').value,
            password: document.getElementById('loginPassword').value
        };

        try {
            const res = await fetch('/api/auth/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });

            const json = await res.json();
            if (res.ok) {
                Utils.showToast(`Welcome back, ${json.data.username}!`, 'success');
                document.getElementById('loginForm').reset();
                UI.showAuthenticatedView(json.data);
            } else {
                Utils.showToast(json.message || 'Login failed', 'error');
            }
        } catch (err) {
            Utils.showToast('Network error while logging in.', 'error');
        } finally {
            submitBtn.disabled = false;
        }
    },

    async register(event) {
        event.preventDefault();
        const submitBtn = document.getElementById('regSubmitBtn');
        const password = document.getElementById('regPassword').value;
        const confirmPassword = document.getElementById('regConfirmPassword').value;

        if (password !== confirmPassword) {
            Utils.showToast('Passwords do not match.', 'warning');
            return;
        }

        submitBtn.disabled = true;

        const payload = {
            username: document.getElementById('regUsername').value,
            email: document.getElementById('regEmail').value,
            password: password,
            confirmPassword: confirmPassword
        };

        try {
            const res = await fetch('/api/auth/register', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });

            const json = await res.json();
            if (res.ok) {
                Utils.showToast('Account created successfully! Please sign in.', 'success');
                document.getElementById('registerForm').reset();

                // Switch to login tab
                const loginTab = new bootstrap.Tab(document.getElementById('login-tab'));
                loginTab.show();
                document.getElementById('loginIdentifier').value = payload.username;
            } else {
                const errorMsg = json.fieldErrors ? Object.values(json.fieldErrors).join(', ') : json.message;
                Utils.showToast(errorMsg || 'Registration failed', 'error');
            }
        } catch (err) {
            Utils.showToast('Network error while creating account.', 'error');
        } finally {
            submitBtn.disabled = false;
        }
    },

    async logout() {
        try {
            await fetch('/api/auth/logout', { method: 'POST' });
        } catch (e) {
            // ignore
        }
        Utils.showToast('Logged out successfully.', 'info');
        UI.showAuthView();
    }
};

// ================= DASHBOARD & CHARTS MODULE =================
const DashboardModule = {
    async refreshDashboard() {
        await Promise.all([
            this.loadSummary(),
            this.loadCategoryChart(),
            this.loadMonthlyChart()
        ]);
    },

    async loadSummary() {
        try {
            const res = await fetch('/api/dashboard/summary');
            if (!res.ok) return;

            const json = await res.json();
            const data = json.data;

            document.getElementById('kpiIncome').textContent = Utils.formatCurrency(data.totalIncome);
            document.getElementById('kpiExpense').textContent = Utils.formatCurrency(data.totalExpense);

            const balanceEl = document.getElementById('kpiBalance');
            balanceEl.textContent = Utils.formatCurrency(data.netBalance);
            if (data.netBalance < 0) {
                balanceEl.className = 'stat-value text-danger';
            } else {
                balanceEl.className = 'stat-value text-primary';
            }

            this.renderRecentTransactions(data.recentTransactions);
        } catch (err) {
            console.error('Failed to load dashboard summary', err);
        }
    },

    renderRecentTransactions(transactions) {
        const tbody = document.getElementById('recentTransactionsTableBody');
        if (!transactions || transactions.length === 0) {
            tbody.innerHTML = `<tr><td colspan="6" class="text-center text-muted py-4">No recent transactions recorded yet.</td></tr>`;
            return;
        }

        tbody.innerHTML = transactions.map(t => `
            <tr>
                <td>${Utils.formatDate(t.transactionDate)}</td>
                <td>
                    <span class="badge ${t.type === 'INCOME' ? 'badge-income' : 'badge-expense'}">
                        ${t.type}
                    </span>
                </td>
                <td class="fw-semibold">${t.categoryName}</td>
                <td>${t.description ? t.description : '<span class="text-muted fst-italic">None</span>'}</td>
                <td class="text-end fw-bold ${t.type === 'INCOME' ? 'text-success' : 'text-danger'}">
                    ${t.type === 'INCOME' ? '+' : '-'}${Utils.formatCurrency(t.amount)}
                </td>
                <td class="text-center">
                    <button class="btn btn-outline-primary btn-sm me-1" title="Edit" onclick="TransactionModule.openEditModal(${t.id})">
                        <i class="bi bi-pencil"></i>
                    </button>
                    <button class="btn btn-outline-danger btn-sm" title="Delete" onclick="TransactionModule.openDeleteModal(${t.id})">
                        <i class="bi bi-trash"></i>
                    </button>
                </td>
            </tr>
        `).join('');
    },

    async loadCategoryChart() {
        try {
            const res = await fetch('/api/dashboard/category-expenses');
            if (!res.ok) return;

            const json = await res.json();
            const list = json.data;

            const chartCanvas = document.getElementById('categoryChart');
            const noDataEl = document.getElementById('noCategoryData');

            if (!list || list.length === 0) {
                chartCanvas.classList.add('d-none');
                noDataEl.classList.remove('d-none');
                if (categoryChartInstance) {
                    categoryChartInstance.destroy();
                    categoryChartInstance = null;
                }
                return;
            }

            chartCanvas.classList.remove('d-none');
            noDataEl.classList.add('d-none');

            const labels = list.map(item => item.categoryName);
            const dataValues = list.map(item => item.totalAmount);

            const palette = [
                '#ef4444', '#f97316', '#f59e0b', '#10b981', '#06b6d4',
                '#3b82f6', '#6366f1', '#8b5cf6', '#ec4899', '#64748b'
            ];

            if (categoryChartInstance) {
                categoryChartInstance.destroy();
            }

            const ctx = chartCanvas.getContext('2d');
            categoryChartInstance = new Chart(ctx, {
                type: 'doughnut',
                data: {
                    labels: labels,
                    datasets: [{
                        data: dataValues,
                        backgroundColor: palette.slice(0, labels.length),
                        borderWidth: 2,
                        borderColor: '#ffffff'
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    plugins: {
                        legend: { position: 'bottom' },
                        tooltip: {
                            callbacks: {
                                label: function(context) {
                                    return ` ${context.label}: ₹${context.raw.toLocaleString('en-IN')}`;
                                }
                            }
                        }
                    }
                }
            });
        } catch (err) {
            console.error('Failed to load category chart', err);
        }
    },

    async loadMonthlyChart() {
        try {
            const res = await fetch('/api/dashboard/monthly-summary');
            if (!res.ok) return;

            const json = await res.json();
            const list = json.data;

            const chartCanvas = document.getElementById('monthlyChart');
            const labels = list.map(item => item.monthLabel);
            const incomeData = list.map(item => item.income);
            const expenseData = list.map(item => item.expense);

            if (monthlyChartInstance) {
                monthlyChartInstance.destroy();
            }

            const ctx = chartCanvas.getContext('2d');
            monthlyChartInstance = new Chart(ctx, {
                type: 'bar',
                data: {
                    labels: labels,
                    datasets: [
                        {
                            label: 'Income',
                            data: incomeData,
                            backgroundColor: '#10b981',
                            borderRadius: 6
                        },
                        {
                            label: 'Expense',
                            data: expenseData,
                            backgroundColor: '#ef4444',
                            borderRadius: 6
                        }
                    ]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    scales: {
                        y: {
                            beginAtZero: true,
                            ticks: {
                                callback: value => '₹' + value.toLocaleString('en-IN')
                            }
                        }
                    },
                    plugins: {
                        legend: { position: 'top' },
                        tooltip: {
                            callbacks: {
                                label: function(context) {
                                    return ` ${context.dataset.label}: ₹${context.raw.toLocaleString('en-IN')}`;
                                }
                            }
                        }
                    }
                }
            });
        } catch (err) {
            console.error('Failed to load monthly chart', err);
        }
    }
};

// ================= TRANSACTION MANAGEMENT MODULE =================
const TransactionModule = {
    async populateFilterCategories() {
        try {
            const res = await fetch('/api/categories');
            if (!res.ok) return;
            const json = await res.json();

            const select = document.getElementById('filterCategory');
            select.innerHTML = '<option value="">All Categories</option>' +
                json.data.map(c => `<option value="${c.id}">${c.name} (${c.type})</option>`).join('');
        } catch (err) {
            console.error('Failed to load filter categories', err);
        }
    },

    async loadModalCategories(type, selectedCategoryId = null) {
        const select = document.getElementById('txCategory');
        select.innerHTML = '<option value="">Loading categories...</option>';

        try {
            const res = await fetch(`/api/categories?type=${type}`);
            if (!res.ok) throw new Error();
            const json = await res.json();

            select.innerHTML = '<option value="">Select a category</option>' +
                json.data.map(c => `
                    <option value="${c.id}" ${selectedCategoryId && selectedCategoryId === c.id ? 'selected' : ''}>
                        ${c.name}
                    </option>
                `).join('');
        } catch (err) {
            select.innerHTML = '<option value="">Failed to load categories</option>';
        }
    },

    async openModal(type = 'EXPENSE') {
        document.getElementById('transactionForm').reset();
        document.getElementById('txId').value = '';
        document.getElementById('transactionModalTitle').textContent = 'Add Transaction';
        document.getElementById('txSaveBtn').textContent = 'Save Transaction';

        // Select radio button
        if (type === 'INCOME') {
            document.getElementById('txTypeIncome').checked = true;
        } else {
            document.getElementById('txTypeExpense').checked = true;
        }

        document.getElementById('txDate').value = Utils.getTodayDate();
        await this.loadModalCategories(type);

        transactionModalInstance.show();
    },

    async openEditModal(id) {
        try {
            const res = await fetch(`/api/transactions/${id}`);
            if (!res.ok) {
                Utils.showToast('Could not load transaction details.', 'error');
                return;
            }

            const json = await res.json();
            const t = json.data;

            document.getElementById('txId').value = t.id;
            document.getElementById('transactionModalTitle').textContent = 'Edit Transaction';
            document.getElementById('txSaveBtn').textContent = 'Update Transaction';

            if (t.type === 'INCOME') {
                document.getElementById('txTypeIncome').checked = true;
            } else {
                document.getElementById('txTypeExpense').checked = true;
            }

            await this.loadModalCategories(t.type, t.categoryId);

            document.getElementById('txAmount').value = t.amount;
            document.getElementById('txDate').value = t.transactionDate;
            document.getElementById('txDescription').value = t.description || '';

            transactionModalInstance.show();
        } catch (err) {
            Utils.showToast('Error opening edit modal.', 'error');
        }
    },

    async saveTransaction(event) {
        event.preventDefault();
        const saveBtn = document.getElementById('txSaveBtn');
        saveBtn.disabled = true;

        const id = document.getElementById('txId').value;
        const type = document.querySelector('input[name="txType"]:checked').value;
        const categoryId = document.getElementById('txCategory').value;
        const amount = document.getElementById('txAmount').value;
        const transactionDate = document.getElementById('txDate').value;
        const description = document.getElementById('txDescription').value;

        if (!categoryId) {
            Utils.showToast('Please select a category.', 'warning');
            saveBtn.disabled = false;
            return;
        }

        const payload = {
            categoryId: parseInt(categoryId),
            amount: parseFloat(amount),
            type: type,
            transactionDate: transactionDate,
            description: description
        };

        const url = id ? `/api/transactions/${id}` : '/api/transactions';
        const method = id ? 'PUT' : 'POST';

        try {
            const res = await fetch(url, {
                method: method,
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });

            const json = await res.json();
            if (res.ok) {
                Utils.showToast(id ? 'Transaction updated successfully!' : 'Transaction recorded successfully!', 'success');
                transactionModalInstance.hide();

                // Refresh both views
                DashboardModule.refreshDashboard();
                this.loadTransactions();
            } else {
                const errorMsg = json.fieldErrors ? Object.values(json.fieldErrors).join(', ') : json.message;
                Utils.showToast(errorMsg || 'Failed to save transaction', 'error');
            }
        } catch (err) {
            Utils.showToast('Network error while saving transaction.', 'error');
        } finally {
            saveBtn.disabled = false;
        }
    },

    openDeleteModal(id) {
        pendingDeleteId = id;
        deleteModalInstance.show();
    },

    async executeDelete(id) {
        try {
            const res = await fetch(`/api/transactions/${id}`, { method: 'DELETE' });
            if (res.ok) {
                Utils.showToast('Transaction deleted successfully.', 'success');
                deleteModalInstance.hide();
                pendingDeleteId = null;

                // Refresh dashboard and history
                DashboardModule.refreshDashboard();
                this.loadTransactions();
            } else {
                const json = await res.json();
                Utils.showToast(json.message || 'Failed to delete transaction.', 'error');
            }
        } catch (err) {
            Utils.showToast('Network error while deleting transaction.', 'error');
        }
    },

    async loadTransactions(filters = {}) {
        const tbody = document.getElementById('fullTransactionsTableBody');
        tbody.innerHTML = `<tr><td colspan="6" class="text-center py-4 text-muted"><div class="spinner-border spinner-border-sm text-primary me-2"></div>Loading transactions...</td></tr>`;

        const queryParams = new URLSearchParams();
        if (filters.keyword) queryParams.append('keyword', filters.keyword);
        if (filters.type) queryParams.append('type', filters.type);
        if (filters.categoryId) queryParams.append('categoryId', filters.categoryId);
        if (filters.startDate) queryParams.append('startDate', filters.startDate);
        if (filters.endDate) queryParams.append('endDate', filters.endDate);

        try {
            const res = await fetch(`/api/transactions?${queryParams.toString()}`);
            if (!res.ok) throw new Error();

            const json = await res.json();
            const transactions = json.data;

            if (!transactions || transactions.length === 0) {
                tbody.innerHTML = `<tr><td colspan="6" class="text-center py-5 text-muted"><i class="bi bi-inbox fs-3 d-block mb-2"></i>No transactions found matching your criteria.</td></tr>`;
                return;
            }

            tbody.innerHTML = transactions.map(t => `
                <tr>
                    <td>${Utils.formatDate(t.transactionDate)}</td>
                    <td>
                        <span class="badge ${t.type === 'INCOME' ? 'badge-income' : 'badge-expense'}">
                            ${t.type}
                        </span>
                    </td>
                    <td class="fw-semibold">${t.categoryName}</td>
                    <td>${t.description ? t.description : '<span class="text-muted fst-italic">None</span>'}</td>
                    <td class="text-end fw-bold ${t.type === 'INCOME' ? 'text-success' : 'text-danger'}">
                        ${t.type === 'INCOME' ? '+' : '-'}${Utils.formatCurrency(t.amount)}
                    </td>
                    <td class="text-center">
                        <button class="btn btn-outline-primary btn-sm me-1" title="Edit" onclick="TransactionModule.openEditModal(${t.id})">
                            <i class="bi bi-pencil"></i>
                        </button>
                        <button class="btn btn-outline-danger btn-sm" title="Delete" onclick="TransactionModule.openDeleteModal(${t.id})">
                            <i class="bi bi-trash"></i>
                        </button>
                    </td>
                </tr>
            `).join('');
        } catch (err) {
            tbody.innerHTML = `<tr><td colspan="6" class="text-center py-4 text-danger">Failed to load transactions.</td></tr>`;
        }
    },

    applyFilters(event) {
        event.preventDefault();
        const filters = {
            keyword: document.getElementById('filterKeyword').value.trim(),
            type: document.getElementById('filterType').value,
            categoryId: document.getElementById('filterCategory').value,
            startDate: document.getElementById('filterStartDate').value,
            endDate: document.getElementById('filterEndDate').value
        };
        this.loadTransactions(filters);
    },

    resetFilters() {
        document.getElementById('filterForm').reset();
        this.loadTransactions();
    },

    async onFilterTypeChange() {
        const type = document.getElementById('filterType').value;
        const categorySelect = document.getElementById('filterCategory');

        try {
            const url = type ? `/api/categories?type=${type}` : '/api/categories';
            const res = await fetch(url);
            if (!res.ok) return;

            const json = await res.json();
            categorySelect.innerHTML = '<option value="">All Categories</option>' +
                json.data.map(c => `<option value="${c.id}">${c.name}</option>`).join('');
        } catch (err) {
            console.error('Filter category reload failed', err);
        }
    }
};

// ================= DATABASE EXPLORER MODULE =================
const DatabaseModule = {
    currentTable: 'transactions',
    currentSchemaTable: 'transactions',

    async refresh() {
        await Promise.all([
            this.loadInfo(),
            this.selectLiveTable(this.currentTable),
            this.selectSchemaTable(this.currentSchemaTable)
        ]);
    },

    async loadInfo() {
        try {
            const res = await fetch('/api/database/info');
            if (!res.ok) return;
            const json = await res.json();
            const d = json.data;

            document.getElementById('dbInfoName').textContent = d.databaseName || 'smart_expense_tracker';
            document.getElementById('dbInfoStatus').textContent = d.status === 'ONLINE' ? 'MySQL 8.0 (Online)' : d.status;
            document.getElementById('dbUserCount').textContent = d.userCount || 0;
            document.getElementById('dbTxCount').textContent = d.transactionCount || 0;
        } catch (e) {
            console.error('Failed to load DB info', e);
        }
    },

    async selectLiveTable(tableName) {
        this.currentTable = tableName;
        ['transactions', 'users', 'categories'].forEach(t => {
            const btn = document.getElementById('btnTbl' + t.charAt(0).toUpperCase() + t.slice(1));
            if (btn) {
                if (t === tableName) {
                    btn.classList.add('active', 'btn-primary');
                    btn.classList.remove('btn-outline-primary');
                } else {
                    btn.classList.remove('active', 'btn-primary');
                    btn.classList.add('btn-outline-primary');
                }
            }
        });

        document.getElementById('currentTblName').textContent = tableName;
        const thead = document.getElementById('dbLiveTableHead');
        const tbody = document.getElementById('dbLiveTableBody');
        tbody.innerHTML = '<tr><td class="text-center py-3"><div class="spinner-border spinner-border-sm text-primary"></div> Loading...</td></tr>';

        try {
            const res = await fetch(`/api/database/table/${tableName}`);
            if (!res.ok) throw new Error();
            const json = await res.json();
            const data = json.data;

            document.getElementById('currentTblRowCount').textContent = data.rowCount;

            // Render Head
            thead.innerHTML = '<tr>' + data.columns.map(c => `<th>${c}</th>`).join('') + '</tr>';

            // Render Rows
            if (!data.rows || data.rows.length === 0) {
                tbody.innerHTML = `<tr><td colspan="${data.columns.length}" class="text-center text-muted py-4">No rows present in table ${tableName}.</td></tr>`;
                return;
            }

            tbody.innerHTML = data.rows.map(r => {
                return '<tr>' + data.columns.map(c => {
                    let val = r[c];
                    if (val === null || val === undefined) return '<td class="text-muted fst-italic">NULL</td>';
                    if (c === 'password') {
                        return `<td><span class="badge bg-secondary font-monospace" title="${val}">BCrypt: ${val.substring(0, 15)}...</span></td>`;
                    }
                    if (c === 'type') {
                        const bg = val === 'INCOME' ? 'badge-income' : 'badge-expense';
                        return `<td><span class="badge ${bg}">${val}</span></td>`;
                    }
                    if (c === 'amount') {
                        return `<td class="fw-bold">${Utils.formatCurrency(val)}</td>`;
                    }
                    return `<td>${val}</td>`;
                }).join('') + '</tr>';
            }).join('');
        } catch (e) {
            tbody.innerHTML = '<tr><td class="text-danger py-3">Failed to load table data.</td></tr>';
        }
    },

    async selectSchemaTable(tableName) {
        this.currentSchemaTable = tableName;
        ['transactions', 'users', 'categories'].forEach(t => {
            const btn = document.getElementById('btnSchema' + t.charAt(0).toUpperCase() + t.slice(1));
            if (btn) {
                if (t === tableName) {
                    btn.classList.add('active', 'btn-primary');
                    btn.classList.remove('btn-outline-primary');
                } else {
                    btn.classList.remove('active', 'btn-primary');
                    btn.classList.add('btn-outline-primary');
                }
            }
        });

        document.getElementById('currentSchemaTblName').textContent = tableName;
        const tbody = document.getElementById('dbSchemaTableBody');
        tbody.innerHTML = '<tr><td colspan="6" class="text-center py-3">Loading schema...</td></tr>';

        try {
            const res = await fetch(`/api/database/schema/${tableName}`);
            if (!res.ok) throw new Error();
            const json = await res.json();
            const schema = json.data;

            tbody.innerHTML = schema.map(col => `
                <tr>
                    <td class="fw-bold font-monospace text-primary">${col.Field}</td>
                    <td><span class="badge bg-light text-dark border">${col.Type}</span></td>
                    <td>${col.Null === 'YES' ? '<span class="text-success">YES</span>' : '<span class="text-muted">NO</span>'}</td>
                    <td>${col.Key === 'PRI' ? '<span class="badge bg-danger">PRIMARY KEY</span>' : col.Key === 'UNI' ? '<span class="badge bg-info">UNIQUE</span>' : col.Key === 'MUL' ? '<span class="badge bg-warning text-dark">FOREIGN KEY / INDEX</span>' : '<span class="text-muted">-</span>'}</td>
                    <td>${col.Default !== null ? col.Default : '<span class="text-muted">NULL</span>'}</td>
                    <td>${col.Extra ? col.Extra : '-'}</td>
                </tr>
            `).join('');
        } catch (e) {
            tbody.innerHTML = '<tr><td colspan="6" class="text-danger py-3">Failed to load schema.</td></tr>';
        }
    },

    onSelectPresetQuery() {
        const val = document.getElementById('dbPresetQueries').value;
        document.getElementById('dbCustomSql').value = val;
    },

    async executeConsoleQuery() {
        const sql = document.getElementById('dbCustomSql').value.trim();
        if (!sql) {
            Utils.showToast('Please enter an SQL query.', 'warning');
            return;
        }

        const thead = document.getElementById('dbQueryResultHead');
        const tbody = document.getElementById('dbQueryResultBody');
        tbody.innerHTML = '<tr><td class="text-center py-3"><div class="spinner-border spinner-border-sm text-primary"></div> Executing query...</td></tr>';

        try {
            const res = await fetch('/api/database/query', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ query: sql })
            });

            const json = await res.json();
            if (!res.ok) {
                tbody.innerHTML = `<tr><td class="text-danger py-3"><i class="bi bi-x-circle me-1"></i> ${json.message || 'Error executing query'}</td></tr>`;
                thead.innerHTML = '';
                return;
            }

            const data = json.data;
            if (!data.columns || data.columns.length === 0) {
                thead.innerHTML = '';
                tbody.innerHTML = '<tr><td class="text-muted py-3">Query returned 0 columns.</td></tr>';
                return;
            }

            thead.innerHTML = '<tr>' + data.columns.map(c => `<th>${c}</th>`).join('') + '</tr>';

            if (!data.rows || data.rows.length === 0) {
                tbody.innerHTML = `<tr><td colspan="${data.columns.length}" class="text-center text-muted py-4">Query returned 0 rows.</td></tr>`;
                return;
            }

            tbody.innerHTML = data.rows.map(r => {
                return '<tr>' + data.columns.map(c => {
                    let val = r[c];
                    if (val === null || val === undefined) return '<td class="text-muted fst-italic">NULL</td>';
                    return `<td>${val}</td>`;
                }).join('') + '</tr>';
            }).join('');

            Utils.showToast(`Executed query: ${data.rowCount} rows returned.`, 'success');
        } catch (e) {
            tbody.innerHTML = '<tr><td class="text-danger py-3">Failed to execute query.</td></tr>';
        }
    },

    async seedDemoData() {
        try {
            const res = await fetch('/api/database/seed-demo', { method: 'POST' });
            const json = await res.json();
            if (res.ok) {
                Utils.showToast(json.message || 'Demo data seeded successfully!', 'success');
                this.refresh();
                if (currentUser) {
                    DashboardModule.refreshDashboard();
                }
            } else {
                Utils.showToast(json.message || 'Seeding failed.', 'error');
            }
        } catch (e) {
            Utils.showToast('Network error while seeding demo data.', 'error');
        }
    }
};

// ================= BOOTSTRAP INITIALIZATION =================
document.addEventListener('DOMContentLoaded', () => {
    UI.init();
});
