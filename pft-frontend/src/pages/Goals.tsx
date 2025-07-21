import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { PlusIcon, PencilIcon, TrashIcon, CheckCircleIcon, ExclamationTriangleIcon } from '@heroicons/react/24/outline';
import { apiService } from '../services/api';
import type { Goal, CreateGoalForm } from '../types';
import toast from 'react-hot-toast';

export default function Goals() {
  const [isAddModalOpen, setIsAddModalOpen] = useState(false);
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [selectedGoal, setSelectedGoal] = useState<Goal | null>(null);
  const queryClient = useQueryClient();

  // Fetch goals
  const { data: goals = [], isLoading, error } = useQuery<Goal[]>({
    queryKey: ['goals'],
    queryFn: () => apiService.getGoals(),
  });

  // Create goal mutation
  const createGoalMutation = useMutation({
    mutationFn: (data: CreateGoalForm) => apiService.createGoal(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['goals'] });
      queryClient.invalidateQueries({ queryKey: ['dashboard-stats'] });
      setIsAddModalOpen(false);
      toast.success('Goal created successfully!');
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Failed to create goal');
    },
  });

  // Update goal mutation
  const updateGoalMutation = useMutation({
    mutationFn: ({ id, data }: { id: number; data: Partial<CreateGoalForm> }) =>
      apiService.updateGoal(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['goals'] });
      queryClient.invalidateQueries({ queryKey: ['dashboard-stats'] });
      setIsEditModalOpen(false);
      setSelectedGoal(null);
      toast.success('Goal updated successfully!');
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Failed to update goal');
    },
  });

  // Delete goal mutation
  const deleteGoalMutation = useMutation({
    mutationFn: (id: number) => apiService.deleteGoal(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['goals'] });
      queryClient.invalidateQueries({ queryKey: ['dashboard-stats'] });
      toast.success('Goal deleted successfully!');
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Failed to delete goal');
    },
  });

  const handleEdit = (goal: Goal) => {
    setSelectedGoal(goal);
    setIsEditModalOpen(true);
  };

  const handleDelete = (goal: Goal) => {
    if (window.confirm(`Are you sure you want to delete "${goal.name}"?`)) {
      deleteGoalMutation.mutate(goal.id);
    }
  };

  const getGoalTypeColor = (type: string) => {
    const colors = {
      SAVINGS: 'bg-green-100 text-green-800',
      DEBT_PAYOFF: 'bg-red-100 text-red-800',
      EMERGENCY_FUND: 'bg-yellow-100 text-yellow-800',
      INVESTMENT: 'bg-blue-100 text-blue-800',
      PURCHASE: 'bg-purple-100 text-purple-800',
      TRAVEL: 'bg-indigo-100 text-indigo-800',
      EDUCATION: 'bg-pink-100 text-pink-800',
      OTHER: 'bg-gray-100 text-gray-800',
    };
    return colors[type as keyof typeof colors] || 'bg-gray-100 text-gray-800';
  };

  const getStatusColor = (status: string) => {
    const colors = {
      ACTIVE: 'bg-green-100 text-green-800',
      COMPLETED: 'bg-blue-100 text-blue-800',
      PAUSED: 'bg-yellow-100 text-yellow-800',
      CANCELLED: 'bg-red-100 text-red-800',
    };
    return colors[status as keyof typeof colors] || 'bg-gray-100 text-gray-800';
  };

  const getProgressColor = (goal: Goal) => {
    if (goal.isCompleted) return 'bg-blue-500';
    if (goal.isNearCompletion) return 'bg-green-500';
    if (goal.isOverdue) return 'bg-red-500';
    return 'bg-gray-500';
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
    });
  };

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="text-lg text-gray-600">Loading goals...</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="text-lg text-red-600">Error loading goals</div>
      </div>
    );
  }

  const activeGoals = goals.filter(goal => goal.status === 'ACTIVE');
  const completedGoals = goals.filter(goal => goal.status === 'COMPLETED');
  const nearCompletionGoals = goals.filter(goal => goal.isNearCompletion && goal.status === 'ACTIVE');
  const overdueGoals = goals.filter(goal => goal.isOverdue && goal.status === 'ACTIVE');
  const totalTargetAmount = goals.reduce((sum, goal) => sum + goal.targetAmount, 0);
  const totalCurrentAmount = goals.reduce((sum, goal) => sum + goal.currentAmount, 0);

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Goals</h1>
          <p className="text-gray-600">Track your financial goals and progress</p>
        </div>
        <button
          onClick={() => setIsAddModalOpen(true)}
          className="btn-primary flex items-center gap-2"
        >
          <PlusIcon className="h-5 w-5" />
          Add Goal
        </button>
      </div>

      {/* Summary Cards */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
        <div className="card">
          <div className="card-header">
            <h3 className="card-title">Total Target</h3>
          </div>
          <p className="text-3xl font-bold text-blue-600">
            ${totalTargetAmount.toFixed(2)}
          </p>
        </div>
        <div className="card">
          <div className="card-header">
            <h3 className="card-title">Total Saved</h3>
          </div>
          <p className="text-3xl font-bold text-green-600">
            ${totalCurrentAmount.toFixed(2)}
          </p>
        </div>
        <div className="card">
          <div className="card-header">
            <h3 className="card-title">Active Goals</h3>
          </div>
          <p className="text-3xl font-bold text-purple-600">
            {activeGoals.length}
          </p>
        </div>
        <div className="card">
          <div className="card-header">
            <h3 className="card-title">Completed</h3>
          </div>
          <p className="text-3xl font-bold text-blue-600">
            {completedGoals.length}
          </p>
        </div>
      </div>

      {/* Goal Alerts */}
      {(nearCompletionGoals.length > 0 || overdueGoals.length > 0) && (
        <div className="card border-yellow-200 bg-yellow-50">
          <div className="card-header">
            <div className="flex items-center gap-2">
              <ExclamationTriangleIcon className="h-5 w-5 text-yellow-600" />
              <h3 className="card-title text-yellow-800">Goal Alerts</h3>
            </div>
          </div>
          <div className="space-y-2">
            {nearCompletionGoals.map((goal) => (
              <div key={goal.id} className="flex justify-between items-center p-3 bg-green-100 rounded-lg">
                <div>
                  <p className="font-medium text-green-800">{goal.name}</p>
                  <p className="text-sm text-green-600">
                    {goal.percentageComplete.toFixed(0)}% complete - ${goal.currentAmount.toFixed(2)} of ${goal.targetAmount.toFixed(2)}
                  </p>
                </div>
                <CheckCircleIcon className="h-5 w-5 text-green-600" />
              </div>
            ))}
            {overdueGoals.map((goal) => (
              <div key={goal.id} className="flex justify-between items-center p-3 bg-red-100 rounded-lg">
                <div>
                  <p className="font-medium text-red-800">{goal.name}</p>
                  <p className="text-sm text-red-600">
                    Overdue by {goal.daysRemaining} days - ${goal.remainingAmount.toFixed(2)} remaining
                  </p>
                </div>
                <ExclamationTriangleIcon className="h-5 w-5 text-red-600" />
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Goals Table */}
      <div className="card">
        <div className="card-header">
          <h3 className="card-title">All Goals</h3>
        </div>
        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Goal
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Type
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Target
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Current
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Progress
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Target Date
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
              {goals.map((goal) => (
                <tr key={goal.id} className="hover:bg-gray-50">
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div>
                      <div className="text-sm font-medium text-gray-900">{goal.name}</div>
                      {goal.description && (
                        <div className="text-sm text-gray-500 max-w-xs truncate">{goal.description}</div>
                      )}
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <span className={`inline-flex px-2 py-1 text-xs font-semibold rounded-full ${getGoalTypeColor(goal.type)}`}>
                      {goal.type.replace('_', ' ')}
                    </span>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className="text-sm font-semibold text-gray-900">
                      ${goal.targetAmount.toFixed(2)}
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className="text-sm font-semibold text-green-600">
                      ${goal.currentAmount.toFixed(2)}
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className="flex items-center">
                      <div className="w-16 bg-gray-200 rounded-full h-2 mr-2">
                        <div
                          className={`h-2 rounded-full ${getProgressColor(goal)}`}
                          style={{ width: `${goal.percentageComplete}%` }}
                        ></div>
                      </div>
                      <span className="text-sm text-gray-600">
                        {goal.percentageComplete.toFixed(0)}%
                      </span>
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className="text-sm text-gray-900">
                      {goal.targetDate ? formatDate(goal.targetDate) : 'No date'}
                    </div>
                    {goal.daysRemaining !== undefined && goal.daysRemaining > 0 && (
                      <div className="text-xs text-gray-500">
                        {goal.daysRemaining} days left
                      </div>
                    )}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <span className={`inline-flex px-2 py-1 text-xs font-semibold rounded-full ${getStatusColor(goal.status)}`}>
                      {goal.status}
                    </span>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                    <div className="flex justify-end space-x-2">
                      <button
                        onClick={() => handleEdit(goal)}
                        className="text-indigo-600 hover:text-indigo-900"
                      >
                        <PencilIcon className="h-4 w-4" />
                      </button>
                      <button
                        onClick={() => handleDelete(goal)}
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

      {/* Add Goal Modal */}
      {isAddModalOpen && (
        <AddGoalModal
          isOpen={isAddModalOpen}
          onClose={() => setIsAddModalOpen(false)}
          onSubmit={(data) => createGoalMutation.mutate(data)}
          isLoading={createGoalMutation.isPending}
        />
      )}

      {/* Edit Goal Modal */}
      {isEditModalOpen && selectedGoal && (
        <EditGoalModal
          isOpen={isEditModalOpen}
          onClose={() => {
            setIsEditModalOpen(false);
            setSelectedGoal(null);
          }}
          goal={selectedGoal}
          onSubmit={(data) => updateGoalMutation.mutate({ id: selectedGoal.id, data })}
          isLoading={updateGoalMutation.isPending}
        />
      )}
    </div>
  );
}

// Add Goal Modal Component
function AddGoalModal({ isOpen, onClose, onSubmit, isLoading }: {
  isOpen: boolean;
  onClose: () => void;
  onSubmit: (data: CreateGoalForm) => void;
  isLoading: boolean;
}) {
  const [formData, setFormData] = useState<CreateGoalForm>({
    name: '',
    description: '',
    targetAmount: 0,
    currentAmount: 0,
    type: 'SAVINGS',
    targetDate: '',
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
          <h3 className="text-lg font-medium text-gray-900 mb-4">Add New Goal</h3>
          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-gray-700">Goal Name</label>
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
                rows={3}
                className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700">Target Amount</label>
              <input
                type="number"
                step="0.01"
                required
                value={formData.targetAmount}
                onChange={(e) => setFormData({ ...formData, targetAmount: parseFloat(e.target.value) || 0 })}
                className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700">Current Amount</label>
              <input
                type="number"
                step="0.01"
                value={formData.currentAmount}
                onChange={(e) => setFormData({ ...formData, currentAmount: parseFloat(e.target.value) || 0 })}
                className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700">Goal Type</label>
              <select
                value={formData.type}
                onChange={(e) => setFormData({ ...formData, type: e.target.value as any })}
                className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
              >
                <option value="SAVINGS">Savings</option>
                <option value="DEBT_PAYOFF">Debt Payoff</option>
                <option value="EMERGENCY_FUND">Emergency Fund</option>
                <option value="INVESTMENT">Investment</option>
                <option value="PURCHASE">Purchase</option>
                <option value="TRAVEL">Travel</option>
                <option value="EDUCATION">Education</option>
                <option value="OTHER">Other</option>
              </select>
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700">Target Date (Optional)</label>
              <input
                type="date"
                value={formData.targetDate}
                onChange={(e) => setFormData({ ...formData, targetDate: e.target.value })}
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
                {isLoading ? 'Creating...' : 'Create Goal'}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}

// Edit Goal Modal Component
function EditGoalModal({ isOpen, onClose, goal, onSubmit, isLoading }: {
  isOpen: boolean;
  onClose: () => void;
  goal: Goal;
  onSubmit: (data: Partial<CreateGoalForm>) => void;
  isLoading: boolean;
}) {
  const [formData, setFormData] = useState<Partial<CreateGoalForm>>({
    name: goal.name,
    description: goal.description || '',
    targetAmount: goal.targetAmount,
    currentAmount: goal.currentAmount,
    type: goal.type,
    targetDate: goal.targetDate || '',
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
          <h3 className="text-lg font-medium text-gray-900 mb-4">Edit Goal</h3>
          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-gray-700">Goal Name</label>
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
                rows={3}
                className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700">Target Amount</label>
              <input
                type="number"
                step="0.01"
                required
                value={formData.targetAmount}
                onChange={(e) => setFormData({ ...formData, targetAmount: parseFloat(e.target.value) || 0 })}
                className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700">Current Amount</label>
              <input
                type="number"
                step="0.01"
                value={formData.currentAmount}
                onChange={(e) => setFormData({ ...formData, currentAmount: parseFloat(e.target.value) || 0 })}
                className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700">Goal Type</label>
              <select
                value={formData.type}
                onChange={(e) => setFormData({ ...formData, type: e.target.value as any })}
                className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
              >
                <option value="SAVINGS">Savings</option>
                <option value="DEBT_PAYOFF">Debt Payoff</option>
                <option value="EMERGENCY_FUND">Emergency Fund</option>
                <option value="INVESTMENT">Investment</option>
                <option value="PURCHASE">Purchase</option>
                <option value="TRAVEL">Travel</option>
                <option value="EDUCATION">Education</option>
                <option value="OTHER">Other</option>
              </select>
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700">Target Date (Optional)</label>
              <input
                type="date"
                value={formData.targetDate}
                onChange={(e) => setFormData({ ...formData, targetDate: e.target.value })}
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
                {isLoading ? 'Updating...' : 'Update Goal'}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
} 