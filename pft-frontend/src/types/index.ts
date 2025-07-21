// User Types
export interface User {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  phoneNumber?: string;
  dateOfBirth?: string;
  currency: string;
  timeZone: string;
  status: 'ACTIVE' | 'INACTIVE';
  createdAt: string;
  updatedAt: string;
}

export interface AuthResponse {
  success: boolean;
  message: string;
  data: {
    token: string;
    tokenType: string;
    user: User;
  };
  timestamp: string;
}

// Account Types
export interface Account {
  id: number;
  name: string;
  accountNumber?: string;
  institutionName?: string;
  type: 'CHECKING' | 'SAVINGS' | 'CREDIT_CARD' | 'INVESTMENT' | 'LOAN' | 'OTHER';
  status: 'ACTIVE' | 'INACTIVE' | 'CLOSED';
  currentBalance: number;
  initialBalance: number;
  color?: string;
  icon?: string;
  isDefault: boolean;
  createdAt: string;
  updatedAt: string;
  userId: number;
  userFullName: string;
}

// Category Types
export interface Category {
  id: number;
  name: string;
  description?: string;
  type: 'INCOME' | 'EXPENSE' | 'TRANSFER';
  status: 'ACTIVE' | 'INACTIVE';
  color?: string;
  icon?: string;
  isDefault: boolean;
  createdAt: string;
  updatedAt: string;
  userId: number;
  userFullName: string;
}

// Transaction Types
export interface Transaction {
  id: number;
  description: string;
  amount: number;
  type: 'INCOME' | 'EXPENSE' | 'TRANSFER' | 'ADJUSTMENT';
  status: 'PENDING' | 'COMPLETED' | 'CANCELLED' | 'FAILED';
  transactionDate: string;
  referenceNumber?: string;
  notes?: string;
  isRecurring: boolean;
  recurringFrequency?: 'DAILY' | 'WEEKLY' | 'MONTHLY' | 'YEARLY';
  nextRecurringDate?: string;
  createdAt: string;
  updatedAt: string;
  accountId: number;
  accountName: string;
  categoryId?: number;
  categoryName?: string;
  toAccountId?: number;
  toAccountName?: string;
  userId: number;
  userFullName: string;
}

// Budget Types
export interface Budget {
  id: number;
  name: string;
  description?: string;
  amount: number;
  spentAmount: number;
  period: 'MONTHLY' | 'WEEKLY' | 'YEARLY';
  startDate: string;
  endDate?: string;
  alertThreshold?: number;
  isActive: boolean;
  status: 'ACTIVE' | 'INACTIVE' | 'COMPLETED';
  color?: string;
  createdAt: string;
  updatedAt: string;
  userId: number;
  userFullName: string;
  categoryId?: number;
  categoryName?: string;
}

// Goal Types
export interface Goal {
  id: number;
  name: string;
  description?: string;
  targetAmount: number;
  currentAmount: number;
  type: 'SAVINGS' | 'DEBT_PAYOFF' | 'EMERGENCY_FUND' | 'INVESTMENT' | 'PURCHASE' | 'TRAVEL' | 'EDUCATION' | 'OTHER';
  status: 'ACTIVE' | 'COMPLETED' | 'PAUSED' | 'CANCELLED';
  targetDate?: string;
  color?: string;
  icon?: string;
  createdAt: string;
  updatedAt: string;
  userId: number;
  userFullName: string;
  remainingAmount: number;
  percentageComplete: number;
  isCompleted: boolean;
  isOverdue: boolean;
  isNearCompletion: boolean;
  daysRemaining: number;
}

// Form Types
export interface LoginForm {
  email: string;
  password: string;
}

export interface RegisterForm {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  phoneNumber?: string;
  dateOfBirth?: string;
  currency: string;
  timeZone: string;
}

export interface CreateAccountForm {
  name: string;
  type: Account['type'];
  initialBalance: number;
  accountNumber?: string;
  institutionName?: string;
}

export interface CreateCategoryForm {
  name: string;
  description?: string;
  type: Category['type'];
}

export interface CreateTransactionForm {
  description: string;
  amount: number;
  type: Transaction['type'];
  accountId: number;
  categoryId?: number;
  transactionDate: string;
  notes?: string;
}

export interface CreateBudgetForm {
  name: string;
  description?: string;
  amount: number;
  period: Budget['period'];
  categoryId?: number;
  startDate: string;
  endDate: string;
  alertThreshold?: number;
}

export interface CreateGoalForm {
  name: string;
  description?: string;
  targetAmount: number;
  currentAmount?: number;
  type: Goal['type'];
  targetDate?: string;
}

// API Response Types
export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
}

export interface PaginatedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

// Dashboard Types
export interface DashboardStats {
  totalBalance: number;
  monthlyIncome: number;
  monthlyExpenses: number;
  netWorth: number;
  activeBudgets: number;
  activeGoals: number;
  recentTransactions: Transaction[];
  budgetAlerts: Budget[];
  goalAlerts: Goal[];
} 