package com.pft.controller;

import com.pft.dto.AccountDto;
import com.pft.dto.BudgetDto;
import com.pft.dto.GoalDto;
import com.pft.dto.TransactionDto;
import com.pft.entity.Transaction;
import com.pft.service.AccountService;
import com.pft.service.BudgetService;
import com.pft.service.GoalService;
import com.pft.service.TransactionService;
import com.pft.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Dashboard statistics and overview endpoints")
public class DashboardController {
    
    private final AccountService accountService;
    private final TransactionService transactionService;
    private final BudgetService budgetService;
    private final GoalService goalService;
    private final UserService userService;
    
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats(Authentication authentication) {
        String userEmail = authentication.getName();
        Long userId = userService.getUserEntityByEmail(userEmail).getId();
        
        Map<String, Object> stats = new HashMap<>();
        
        // Account statistics
        BigDecimal totalBalance = accountService.getTotalBalanceByUserId(userId);
        List<AccountDto> activeAccounts = accountService.getActiveAccountsByUserId(userId);
        
        // Transaction statistics for current month
        YearMonth currentMonth = YearMonth.now();
        List<TransactionDto> allTransactions = transactionService.getTransactionsByUserId(userId);
        List<TransactionDto> monthlyTransactions = allTransactions.stream()
                .filter(t -> {
                    LocalDateTime transactionDate = t.getTransactionDate();
                    return transactionDate.getYear() == currentMonth.getYear() && 
                           transactionDate.getMonth() == currentMonth.getMonth();
                })
                .collect(java.util.stream.Collectors.toList());
        
        BigDecimal monthlyIncome = BigDecimal.ZERO;
        BigDecimal monthlyExpenses = BigDecimal.ZERO;
        
        for (TransactionDto transaction : monthlyTransactions) {
            if (transaction.getType() == Transaction.TransactionType.INCOME) {
                monthlyIncome = monthlyIncome.add(transaction.getAmount());
            } else if (transaction.getType() == Transaction.TransactionType.EXPENSE) {
                monthlyExpenses = monthlyExpenses.add(transaction.getAmount().abs());
            }
        }
        
        // Budget statistics
        List<BudgetDto> activeBudgets = budgetService.getActiveBudgetsByUserId(userId);
        List<BudgetDto> budgetAlerts = budgetService.getOverBudgetBudgetsByUserId(userId);
        
        // Goal statistics
        List<GoalDto> activeGoals = goalService.getActiveGoalsByUserId(userId);
        List<GoalDto> goalAlerts = goalService.getGoalsNearCompletionByUserId(userId);
        
        // Recent transactions (last 5) - get all and limit to 5
        List<TransactionDto> recentTransactions = allTransactions.stream()
                .sorted((t1, t2) -> t2.getTransactionDate().compareTo(t1.getTransactionDate()))
                .limit(5)
                .collect(java.util.stream.Collectors.toList());
        
        // Calculate net worth (total balance)
        BigDecimal netWorth = totalBalance;
        
        stats.put("totalBalance", totalBalance);
        stats.put("monthlyIncome", monthlyIncome);
        stats.put("monthlyExpenses", monthlyExpenses);
        stats.put("netWorth", netWorth);
        stats.put("activeBudgets", activeBudgets.size());
        stats.put("activeGoals", activeGoals.size());
        stats.put("recentTransactions", recentTransactions);
        stats.put("budgetAlerts", budgetAlerts);
        stats.put("goalAlerts", goalAlerts);
        
        return ResponseEntity.ok(stats);
    }
} 