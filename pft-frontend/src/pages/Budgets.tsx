import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { PlusIcon, PencilIcon, TrashIcon, ExclamationTriangleIcon } from '@heroicons/react/24/outline';
import { apiService } from '../services/api';
import type { Budget, CreateBudgetForm, Category } from '../types';
import toast from 'react-hot-toast';

export default function Budgets() {
  const [isAddModalOpen, setIsAddModalOpen] = useState(false);
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [selectedBudget, setSelectedBudget] = useState<Budget | null>(null);
  const queryClient = useQueryClient();

  // Fetch budgets
  const { data: budgets = [], isLoading, error } = useQuery<Budget[]>({
    queryKey: ['budgets'],
    queryFn: () => apiService.getBudgets(),
  });

  // Fetch categories for budget forms
  const { data: categories = [] } = useQuery<Category[]>({
    queryKey: ['categories'],
    queryFn: () => apiService.getCategories(),
  });

  // Create budget mutation
  const createBudgetMutation = useMutation({
    mutationFn: (data: CreateBudgetForm) => apiService.createBudget(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['budgets'] });
      queryClient.invalidateQueries({ queryKey: ['dashboard-stats'] });
      setIsAddModalOpen(false);
      toast.success('Budget created successfully!');
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Failed to create budget');
    },
  });

  // Update budget mutation
  const updateBudgetMutation = useMutation({
    mutationFn: ({ id, data }: { id: number; data: Partial<CreateBudgetForm> }) =>
      apiService.updateBudget(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['budgets'] });
      queryClient.invalidateQueries({ queryKey: ['dashboard-stats'] });
      setIsEditModalOpen(false);
      setSelectedBudget(null);
      toast.success('Budget updated successfully!');
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Failed to update budget');
    },
  });

  // Delete budget mutation
  const deleteBudgetMutation = useMutation({
    mutationFn: (id: number) => apiService.deleteBudget(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['budgets'] });
      queryClient.invalidateQueries({ queryKey: ['dashboard-stats'] });
      toast.success('Budget deleted successfully!');
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Failed to delete budget');
    },
  });

  const handleEdit = (budget: Budget) => {
    setSelectedBudget(budget);
    setIsEditModalOpen(true);
  };

  const handleDelete = (budget: Budget) => {
    if (window.confirm(`Are you sure you want to delete "${budget.name}"?`)) {
      deleteBudgetMutation.mutate(budget.id);
    }
  };

  const getBudgetPeriodColor = (period: string) => {
    const colors = {
      MONTHLY: 'bg-blue-100 text-blue-800',
      WEEKLY: 'bg-green-100 text-green-800',
      YEARLY: 'bg-purple-100 text-purple-800',
      QUARTERLY: 'bg-yellow-100 text-yellow-800',
    };
    return colors[period as keyof typeof colors] || 'bg-gray-100 text-gray-800';
  };

  const getStatusColor = (status: string) => {
    return status === 'ACTIVE' ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800';
  };

  const calculateProgress = (budget: Budget) => {
    const percentage = (budget.spentAmount / budget.amount) * 100;
    return Math.min(percentage, 100);
  };

  const getProgressColor = (budget: Budget) => {
    const percentage = calculateProgress(budget);
    if (percentage >= 100) return 'bg-red-500';
    if (percentage >= 80) return 'bg-yellow-500';
    return 'bg-green-500';
  };



  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="text-lg text-gray-600">Loading budgets...</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="text-lg text-red-600">Error loading budgets</div>
      </div>
    );
  }

  const activeBudgets = budgets.filter(budget => budget.status === 'ACTIVE');
  const overBudgetBudgets = budgets.filter(budget => budget.spentAmount > budget.amount);
  const totalBudgeted = budgets.reduce((sum, budget) => sum + budget.amount, 0);
  const totalSpent = budgets.reduce((sum, budget) => sum + budget.spentAmount, 0);

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Budgets</h1>
          <p className="text-gray-600">Track your spending against budget limits</p>
        </div>
        <button
          onClick={() => setIsAddModalOpen(true)}
          className="btn-primary flex items-center gap-2"
        >
          <PlusIcon className="h-5 w-5" />
          Add Budget
        </button>
      </div>

      {/* Summary Cards */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
        <div className="card">
          <div className="card-header">
            <h3 className="card-title">Total Budgeted</h3>
          </div>
          <p className="text-3xl font-bold text-blue-600">
            ${totalBudgeted.toFixed(2)}
          </p>
        </div>
        <div className="card">
          <div className="card-header">
            <h3 className="card-title">Total Spent</h3>
          </div>
          <p className="text-3xl font-bold text-green-600">
            ${totalSpent.toFixed(2)}
          </p>
        </div>
        <div className="card">
          <div className="card-header">
            <h3 className="card-title">Active Budgets</h3>
          </div>
          <p className="text-3xl font-bold text-purple-600">
            {activeBudgets.length}
          </p>
        </div>
        <div className="card">
          <div className="card-header">
            <h3 className="card-title">Over Budget</h3>
          </div>
          <p className="text-3xl font-bold text-red-600">
            {overBudgetBudgets.length}
          </p>
        </div>
      </div>

      {/* Budget Alerts */}
      {overBudgetBudgets.length > 0 && (
        <div className="card border-red-200 bg-red-50">
          <div className="card-header">
            <div className="flex items-center gap-2">
              <ExclamationTriangleIcon className="h-5 w-5 text-red-600" />
              <h3 className="card-title text-red-800">Budget Alerts</h3>
            </div>
          </div>
          <div className="space-y-2">
            {overBudgetBudgets.map((budget) => (
              <div key={budget.id} className="flex justify-between items-center p-3 bg-red-100 rounded-lg">
                <div>
                  <p className="font-medium text-red-800">{budget.name}</p>
                  <p className="text-sm text-red-600">
                    Spent ${budget.spentAmount.toFixed(2)} of ${budget.amount.toFixed(2)}
                  </p>
                </div>
                <span className="text-red-800 font-semibold">
                  ${(budget.spentAmount - budget.amount).toFixed(2)} over
                </span>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Budgets Table */}
      <div className="card">
        <div className="card-header">
          <h3 className="card-title">All Budgets</h3>
        </div>
        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Budget
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Category
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Amount
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Spent
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Progress
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Period
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Status
                </th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Actions
                </th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {budgets.map((budget) => (
                <tr key={budget.id} className="hover:bg-gray-50">
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div>
                      <div className="text-sm font-medium text-gray-900">{budget.name}</div>
                      {budget.description && (
                        <div className="text-sm text-gray-500 max-w-xs truncate">{budget.description}</div>
                      )}
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className="text-sm text-gray-900">
                      {budget.categoryName || 'No category'}
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className="text-sm font-semibold text-gray-900">
                      ${budget.amount.toFixed(2)}
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className={`text-sm font-semibold ${
                      budget.spentAmount > budget.amount ? 'text-red-600' : 'text-green-600'
                    }`}>
                      ${budget.spentAmount.toFixed(2)}
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className="flex items-center">
                      <div className="w-16 bg-gray-200 rounded-full h-2 mr-2">
                        <div
                          className={`h-2 rounded-full ${getProgressColor(budget)}`}
                          style={{ width: `${calculateProgress(budget)}%` }}
                        ></div>
                      </div>
                      <span className="text-sm text-gray-600">
                        {calculateProgress(budget).toFixed(0)}%
                      </span>
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <span className={`inline-flex px-2 py-1 text-xs font-semibold rounded-full ${getBudgetPeriodColor(budget.period)}`}>
                      {budget.period}
                    </span>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <span className={`inline-flex px-2 py-1 text-xs font-semibold rounded-full ${getStatusColor(budget.status)}`}>
                      {budget.status}
                    </span>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                    <div className="flex justify-end space-x-2">
                      <button
                        onClick={() => handleEdit(budget)}
                        className="text-indigo-600 hover:text-indigo-900"
                      >
                        <PencilIcon className="h-4 w-4" />
                      </button>
                      <button
                        onClick={() => handleDelete(budget)}
                        className="text-red-600 hover:text-red-900"
                      >
                        <TrashIcon className="h-4 w-4" />
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* Add Budget Modal */}
      {isAddModalOpen && (
        <AddBudgetModal
          isOpen={isAddModalOpen}
          onClose={() => setIsAddModalOpen(false)}
          onSubmit={(data) => createBudgetMutation.mutate(data)}
          isLoading={createBudgetMutation.isPending}
          categories={categories}
        />
      )}

      {/* Edit Budget Modal */}
      {isEditModalOpen && selectedBudget && (
        <EditBudgetModal
          isOpen={isEditModalOpen}
          onClose={() => {
            setIsEditModalOpen(false);
            setSelectedBudget(null);
          }}
          budget={selectedBudget}
          onSubmit={(data) => updateBudgetMutation.mutate({ id: selectedBudget.id, data })}
          isLoading={updateBudgetMutation.isPending}
          categories={categories}
        />
      )}
    </div>
  );
}

// Add Budget Modal Component
function AddBudgetModal({ isOpen, onClose, onSubmit, isLoading, categories }: {
  isOpen: boolean;
  onClose: () => void;
  onSubmit: (data: CreateBudgetForm) => void;
  isLoading: boolean;
  categories: Category[];
}) {
  const [formData, setFormData] = useState<CreateBudgetForm>({
    name: '',
    description: '',
    amount: 0,
    period: 'MONTHLY',
    categoryId: undefined,
    startDate: new Date().toISOString(),
    endDate: new Date(new Date().setMonth(new Date().getMonth() + 1)).toISOString(),
    alertThreshold: 80,
  });

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onSubmit(formData);
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full z-50">
      <div className="relative top-20 mx-auto p-5 border w-96 shadow-lg rounded-md bg-white">
        <div className="mt-3">
          <h3 className="text-lg font-medium text-gray-900 mb-4">Add New Budget</h3>
          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-gray-700">Budget Name</label>
              <input
                type="text"
                required
                value={formData.name}
                onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700">Description (Optional)</label>
              <textarea
                value={formData.description}
                onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                rows={2}
                className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700">Amount</label>
              <input
                type="number"
                step="0.01"
                required
                value={formData.amount}
                onChange={(e) => setFormData({ ...formData, amount: parseFloat(e.target.value) || 0 })}
                className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700">Period</label>
              <select
                value={formData.period}
                onChange={(e) => setFormData({ ...formData, period: e.target.value as any })}
                className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
              >
                <option value="WEEKLY">Weekly</option>
                <option value="MONTHLY">Monthly</option>
                <option value="QUARTERLY">Quarterly</option>
                <option value="YEARLY">Yearly</option>
              </select>
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700">Category (Optional)</label>
              <select
                value={formData.categoryId || ''}
                onChange={(e) => setFormData({ ...formData, categoryId: e.target.value ? parseInt(e.target.value) : undefined })}
                className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
              >
                <option value="">No Category</option>
                {categories
                  .filter(cat => cat.type === 'EXPENSE')
                  .map(category => (
                    <option key={category.id} value={category.id}>
                      {category.name}
                    </option>
                  ))}
              </select>
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700">Start Date</label>
              <input
                type="datetime-local"
                required
                value={formData.startDate ? formData.startDate.slice(0, 16) : ''}
                onChange={(e) => setFormData({ ...formData, startDate: new Date(e.target.value).toISOString() })}
                className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700">End Date</label>
              <input
                type="datetime-local"
                required
                value={formData.endDate ? formData.endDate.slice(0, 16) : ''}
                onChange={(e) => setFormData({ ...formData, endDate: new Date(e.target.value).toISOString() })}
                className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700">Alert Threshold (%)</label>
              <input
                type="number"
                min="0"
                max="100"
                value={formData.alertThreshold}
                onChange={(e) => setFormData({ ...formData, alertThreshold: parseInt(e.target.value) || 80 })}
                className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
              />
            </div>
            <div className="flex justify-end space-x-3 pt-4">
              <button
                type="button"
                onClick={onClose}
                className="btn-secondary"
                disabled={isLoading}
              >
                Cancel
              </button>
              <button
                type="submit"
                className="btn-primary"
                disabled={isLoading}
              >
                {isLoading ? 'Creating...' : 'Create Budget'}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}

// Edit Budget Modal Component
function EditBudgetModal({ isOpen, onClose, budget, onSubmit, isLoading, categories }: {
  isOpen: boolean;
  onClose: () => void;
  budget: Budget;
  onSubmit: (data: Partial<CreateBudgetForm>) => void;
  isLoading: boolean;
  categories: Category[];
}) {
  const [formData, setFormData] = useState<Partial<CreateBudgetForm>>({
    name: budget.name,
    description: budget.description || '',
    amount: budget.amount,
    period: budget.period,
    categoryId: budget.categoryId,
    startDate: budget.startDate,
    endDate: budget.endDate || new Date().toISOString(),
    alertThreshold: budget.alertThreshold,
  });

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onSubmit(formData);
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full z-50">
      <div className="relative top-20 mx-auto p-5 border w-96 shadow-lg rounded-md bg-white">
        <div className="mt-3">
          <h3 className="text-lg font-medium text-gray-900 mb-4">Edit Budget</h3>
          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-gray-700">Budget Name</label>
              <input
                type="text"
                required
                value={formData.name}
                onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700">Description (Optional)</label>
              <textarea
                value={formData.description}
                onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                rows={2}
                className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700">Amount</label>
              <input
                type="number"
                step="0.01"
                required
                value={formData.amount}
                onChange={(e) => setFormData({ ...formData, amount: parseFloat(e.target.value) || 0 })}
                className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700">Period</label>
              <select
                value={formData.period}
                onChange={(e) => setFormData({ ...formData, period: e.target.value as any })}
                className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
              >
                <option value="WEEKLY">Weekly</option>
                <option value="MONTHLY">Monthly</option>
                <option value="QUARTERLY">Quarterly</option>
                <option value="YEARLY">Yearly</option>
              </select>
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700">Category (Optional)</label>
              <select
                value={formData.categoryId || ''}
                onChange={(e) => setFormData({ ...formData, categoryId: e.target.value ? parseInt(e.target.value) : undefined })}
                className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
              >
                <option value="">No Category</option>
                {categories
                  .filter(cat => cat.type === 'EXPENSE')
                  .map(category => (
                    <option key={category.id} value={category.id}>
                      {category.name}
                    </option>
                  ))}
              </select>
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700">Start Date</label>
              <input
                type="datetime-local"
                required
                value={formData.startDate ? formData.startDate.slice(0, 16) : ''}
                onChange={(e) => setFormData({ ...formData, startDate: new Date(e.target.value).toISOString() })}
                className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700">End Date</label>
              <input
                type="datetime-local"
                required
                value={formData.endDate ? formData.endDate.slice(0, 16) : ''}
                onChange={(e) => setFormData({ ...formData, endDate: new Date(e.target.value).toISOString() })}
                className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700">Alert Threshold (%)</label>
              <input
                type="number"
                min="0"
                max="100"
                value={formData.alertThreshold}
                onChange={(e) => setFormData({ ...formData, alertThreshold: parseInt(e.target.value) || 80 })}
                className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
              />
            </div>
            <div className="flex justify-end space-x-3 pt-4">
              <button
                type="button"
                onClick={onClose}
                className="btn-secondary"
                disabled={isLoading}
              >
                Cancel
              </button>
              <button
                type="submit"
                className="btn-primary"
                disabled={isLoading}
              >
                {isLoading ? 'Updating...' : 'Update Budget'}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
} 