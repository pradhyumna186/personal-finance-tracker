import { useQuery } from '@tanstack/react-query';
import { apiService } from '../services/api';
import type { DashboardStats } from '../types';

export default function Dashboard() {
  const { data: stats, isLoading, error } = useQuery<DashboardStats>({
    queryKey: ['dashboard-stats'],
    queryFn: () => apiService.getDashboardStats(),
  });

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="text-lg text-gray-600">Loading dashboard...</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="text-lg text-red-600">Error loading dashboard</div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Dashboard</h1>
        <p className="text-gray-600">Welcome to your personal finance tracker</p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <div className="card">
          <div className="card-header">
            <h3 className="card-title">Total Balance</h3>
          </div>
          <p className="text-3xl font-bold text-green-600">
            ${stats?.totalBalance?.toFixed(2) || '0.00'}
          </p>
        </div>

        <div className="card">
          <div className="card-header">
            <h3 className="card-title">Monthly Income</h3>
          </div>
          <p className="text-3xl font-bold text-blue-600">
            ${stats?.monthlyIncome?.toFixed(2) || '0.00'}
          </p>
        </div>

        <div className="card">
          <div className="card-header">
            <h3 className="card-title">Monthly Expenses</h3>
          </div>
          <p className="text-3xl font-bold text-red-600">
            ${Math.abs(stats?.monthlyExpenses || 0).toFixed(2)}
          </p>
        </div>

        <div className="card">
          <div className="card-header">
            <h3 className="card-title">Net Worth</h3>
          </div>
          <p className="text-3xl font-bold text-purple-600">
            ${stats?.netWorth?.toFixed(2) || '0.00'}
          </p>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div className="card">
          <div className="card-header">
            <h3 className="card-title">Recent Transactions</h3>
          </div>
          <div className="space-y-3">
            {stats?.recentTransactions?.slice(0, 5).map((transaction) => (
              <div key={transaction.id} className="flex items-center justify-between p-3 bg-gray-50 rounded-lg">
                                  <div>
                    <p className="font-medium text-gray-900">{transaction.description}</p>
                    <p className="text-sm text-gray-600">{transaction.accountName}</p>
                    <p className="text-xs text-gray-500">
                      {new Date(transaction.transactionDate).toLocaleDateString()}
                    </p>
                  </div>
                <span className={`font-semibold ${
                  transaction.type === 'INCOME' ? 'text-green-600' : 
                  transaction.type === 'EXPENSE' ? 'text-red-600' : 
                  'text-blue-600'
                }`}>
                  {transaction.type === 'INCOME' ? '+' : 
                   transaction.type === 'EXPENSE' ? '-' : ''}${Math.abs(transaction.amount).toFixed(2)}
                </span>
              </div>
            )) || (
              <p className="text-gray-500 text-center py-4">No recent transactions</p>
            )}
          </div>
        </div>

        <div className="card">
          <div className="card-header">
            <h3 className="card-title">Active Goals</h3>
          </div>
          <div className="space-y-3">
            {stats?.goalAlerts?.slice(0, 5).map((goal) => (
              <div key={goal.id} className="p-3 bg-gray-50 rounded-lg">
                <div className="flex items-center justify-between mb-2">
                  <p className="font-medium text-gray-900">{goal.name}</p>
                  <span className="text-sm text-gray-600">
                    {goal.percentageComplete.toFixed(0)}%
                  </span>
                </div>
                <div className="w-full bg-gray-200 rounded-full h-2">
                  <div
                    className="bg-blue-600 h-2 rounded-full"
                    style={{ width: `${Math.min(goal.percentageComplete, 100)}%` }}
                  />
                </div>
                <p className="text-sm text-gray-600 mt-1">
                  ${goal.currentAmount.toFixed(2)} / ${goal.targetAmount.toFixed(2)}
                </p>
              </div>
            )) || (
              <p className="text-gray-500 text-center py-4">No active goals</p>
            )}
          </div>
        </div>
      </div>
    </div>
  );
} 