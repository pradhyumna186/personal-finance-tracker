import axios from 'axios';
import type { AxiosInstance, AxiosResponse } from 'axios';
import type { 
  AuthResponse, 
  Account, 
  Category, 
  Transaction, 
  Budget, 
  Goal,
  LoginForm,
  RegisterForm,
  CreateAccountForm,
  CreateCategoryForm,
  CreateTransactionForm,
  CreateBudgetForm,
  CreateGoalForm,
  DashboardStats
} from '../types';

class ApiService {
  private api: AxiosInstance;
  private baseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';

  constructor() {
    this.api = axios.create({
      baseURL: this.baseURL,
      headers: {
        'Content-Type': 'application/json',
      },
    });

    // Request interceptor to add auth token
    this.api.interceptors.request.use(
      (config: any) => {
        const token = localStorage.getItem('jwtToken');
        if (token) {
          config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
      },
      (error: any) => {
        return Promise.reject(error);
      }
    );

    // Response interceptor to handle errors
    this.api.interceptors.response.use(
      (response: any) => response,
      (error: any) => {
        if (error.response?.status === 401) {
          localStorage.removeItem('jwtToken');
          window.location.href = '/login';
        }
        return Promise.reject(error);
      }
    );
  }

  // Auth endpoints
  async login(credentials: LoginForm): Promise<AuthResponse> {
    const response: AxiosResponse<AuthResponse> = await this.api.post('/auth/login', credentials);
    return response.data;
  }

  async register(userData: RegisterForm): Promise<AuthResponse> {
    const response: AxiosResponse<AuthResponse> = await this.api.post('/auth/register', userData);
    return response.data;
  }

  // Health check
  async healthCheck(): Promise<any> {
    const response = await this.api.get('/health');
    return response.data;
  }

  // Account endpoints
  async getAccounts(): Promise<Account[]> {
    const response: AxiosResponse<Account[]> = await this.api.get('/accounts');
    return response.data;
  }

  async getAccount(id: number): Promise<Account> {
    const response: AxiosResponse<Account> = await this.api.get(`/accounts/${id}`);
    return response.data;
  }

  async createAccount(accountData: CreateAccountForm): Promise<Account> {
    const response: AxiosResponse<Account> = await this.api.post('/accounts', accountData);
    return response.data;
  }

  async updateAccount(id: number, accountData: Partial<CreateAccountForm>): Promise<Account> {
    const response: AxiosResponse<Account> = await this.api.put(`/accounts/${id}`, accountData);
    return response.data;
  }

  async deleteAccount(id: number): Promise<void> {
    await this.api.delete(`/accounts/${id}`);
  }

  // Category endpoints
  async getCategories(): Promise<Category[]> {
    const response: AxiosResponse<Category[]> = await this.api.get('/categories');
    return response.data;
  }

  async getCategory(id: number): Promise<Category> {
    const response: AxiosResponse<Category> = await this.api.get(`/categories/${id}`);
    return response.data;
  }

  async createCategory(categoryData: CreateCategoryForm): Promise<Category> {
    const response: AxiosResponse<Category> = await this.api.post('/categories', categoryData);
    return response.data;
  }

  async updateCategory(id: number, categoryData: Partial<CreateCategoryForm>): Promise<Category> {
    const response: AxiosResponse<Category> = await this.api.put(`/categories/${id}`, categoryData);
    return response.data;
  }

  async deleteCategory(id: number): Promise<void> {
    await this.api.delete(`/categories/${id}`);
  }

  // Transaction endpoints
  async getTransactions(): Promise<Transaction[]> {
    const response: AxiosResponse<Transaction[]> = await this.api.get('/transactions');
    return response.data;
  }

  async getTransaction(id: number): Promise<Transaction> {
    const response: AxiosResponse<Transaction> = await this.api.get(`/transactions/${id}`);
    return response.data;
  }

  async createTransaction(transactionData: CreateTransactionForm): Promise<Transaction> {
    const response: AxiosResponse<Transaction> = await this.api.post('/transactions', transactionData);
    return response.data;
  }

  async updateTransaction(id: number, transactionData: Partial<CreateTransactionForm>): Promise<Transaction> {
    const response: AxiosResponse<Transaction> = await this.api.put(`/transactions/${id}`, transactionData);
    return response.data;
  }

  async deleteTransaction(id: number): Promise<void> {
    await this.api.delete(`/transactions/${id}`);
  }

  async getTransactionStats(): Promise<any> {
    const response = await this.api.get('/transactions/stats');
    return response.data;
  }

  // Budget endpoints
  async getBudgets(): Promise<Budget[]> {
    const response: AxiosResponse<Budget[]> = await this.api.get('/budgets');
    return response.data;
  }

  async getBudget(id: number): Promise<Budget> {
    const response: AxiosResponse<Budget> = await this.api.get(`/budgets/${id}`);
    return response.data;
  }

  async createBudget(budgetData: CreateBudgetForm): Promise<Budget> {
    const response: AxiosResponse<Budget> = await this.api.post('/budgets', budgetData);
    return response.data;
  }

  async updateBudget(id: number, budgetData: Partial<CreateBudgetForm>): Promise<Budget> {
    const response: AxiosResponse<Budget> = await this.api.put(`/budgets/${id}`, budgetData);
    return response.data;
  }

  async deleteBudget(id: number): Promise<void> {
    await this.api.delete(`/budgets/${id}`);
  }

  // Goal endpoints
  async getGoals(): Promise<Goal[]> {
    const response: AxiosResponse<Goal[]> = await this.api.get('/goals');
    return response.data;
  }

  async getGoal(id: number): Promise<Goal> {
    const response: AxiosResponse<Goal> = await this.api.get(`/goals/${id}`);
    return response.data;
  }

  async createGoal(goalData: CreateGoalForm): Promise<Goal> {
    const response: AxiosResponse<Goal> = await this.api.post('/goals', goalData);
    return response.data;
  }

  async updateGoal(id: number, goalData: Partial<CreateGoalForm>): Promise<Goal> {
    const response: AxiosResponse<Goal> = await this.api.put(`/goals/${id}`, goalData);
    return response.data;
  }

  async deleteGoal(id: number): Promise<void> {
    await this.api.delete(`/goals/${id}`);
  }

  async addGoalProgress(id: number, amount: number): Promise<Goal> {
    const response: AxiosResponse<Goal> = await this.api.post(`/goals/${id}/progress`, { amount });
    return response.data;
  }

  // Dashboard endpoints
  async getDashboardStats(): Promise<DashboardStats> {
    const response: AxiosResponse<DashboardStats> = await this.api.get('/dashboard/stats');
    return response.data;
  }
}

export const apiService = new ApiService();
export default apiService; 