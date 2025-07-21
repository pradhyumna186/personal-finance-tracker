package com.pft.service;

import com.pft.dto.BudgetDto;
import com.pft.entity.Budget;
import com.pft.entity.Category;
import com.pft.entity.User;
import com.pft.repository.BudgetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BudgetService {
    
    private final BudgetRepository budgetRepository;
    private final UserService userService;
    private final CategoryService categoryService;
    
    public BudgetDto getBudgetById(Long budgetId, Long userId) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new RuntimeException("Budget not found with ID: " + budgetId));
        
        // Verify ownership
        if (!budget.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied: Budget does not belong to user");
        }
        
        return BudgetDto.fromEntity(budget);
    }
    
    public List<BudgetDto> getBudgetsByUserId(Long userId) {
        return budgetRepository.findByUserId(userId).stream()
                .map(BudgetDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    public List<BudgetDto> getActiveBudgetsByUserId(Long userId) {
        return budgetRepository.findActiveBudgetsByUserId(userId).stream()
                .map(BudgetDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    public List<BudgetDto> getBudgetsByUserIdAndPeriod(Long userId, Budget.BudgetPeriod period) {
        return budgetRepository.findByUserIdAndPeriod(userId, period).stream()
                .map(BudgetDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    public List<BudgetDto> getBudgetsByUserIdAndDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        return budgetRepository.findBudgetsByUserIdAndDateRange(userId, startDate, endDate).stream()
                .map(BudgetDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public BudgetDto createBudget(BudgetDto budgetDto, Long userId) {
        User user = userService.getUserEntityById(userId);
        
        // Verify category ownership if provided
        Category category = null;
        if (budgetDto.getCategoryId() != null) {
            category = categoryService.getCategoryEntityById(budgetDto.getCategoryId());
            if (!category.getUser().getId().equals(userId)) {
                throw new RuntimeException("Access denied: Category does not belong to user");
            }
        }
        
        Budget budget = new Budget();
        budget.setName(budgetDto.getName());
        budget.setDescription(budgetDto.getDescription());
        budget.setAmount(budgetDto.getAmount());
        budget.setPeriod(budgetDto.getPeriod());
        budget.setStartDate(budgetDto.getStartDate() != null ? budgetDto.getStartDate() : LocalDateTime.now());
        budget.setEndDate(budgetDto.getEndDate());
        budget.setAlertThreshold(budgetDto.getAlertThreshold());
        budget.setUser(user);
        budget.setCategory(category);
        
        Budget savedBudget = budgetRepository.save(budget);
        return BudgetDto.fromEntity(savedBudget);
    }
    
    @Transactional
    public BudgetDto updateBudget(Long budgetId, BudgetDto budgetDto, Long userId) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new RuntimeException("Budget not found with ID: " + budgetId));
        
        // Verify ownership
        if (!budget.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied: Budget does not belong to user");
        }
        
        // Update fields
        if (budgetDto.getName() != null) {
            budget.setName(budgetDto.getName());
        }
        if (budgetDto.getDescription() != null) {
            budget.setDescription(budgetDto.getDescription());
        }
        if (budgetDto.getAmount() != null) {
            budget.setAmount(budgetDto.getAmount());
        }
        if (budgetDto.getEndDate() != null) {
            budget.setEndDate(budgetDto.getEndDate());
        }
        if (budgetDto.getAlertThreshold() != null) {
            budget.setAlertThreshold(budgetDto.getAlertThreshold());
        }
        if (budgetDto.getStatus() != null) {
            budget.setStatus(budgetDto.getStatus());
        }
        
        Budget updatedBudget = budgetRepository.save(budget);
        return BudgetDto.fromEntity(updatedBudget);
    }
    
    @Transactional
    public void deleteBudget(Long budgetId, Long userId) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new RuntimeException("Budget not found with ID: " + budgetId));
        
        // Verify ownership
        if (!budget.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied: Budget does not belong to user");
        }
        
        budgetRepository.delete(budget);
    }
    
    @Transactional
    public void addSpentAmount(Long budgetId, BigDecimal amount, Long userId) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new RuntimeException("Budget not found with ID: " + budgetId));
        
        // Verify ownership
        if (!budget.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied: Budget does not belong to user");
        }
        
        budget.addSpentAmount(amount);
        budgetRepository.save(budget);
    }
    
    @Transactional
    public void resetSpentAmount(Long budgetId, Long userId) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new RuntimeException("Budget not found with ID: " + budgetId));
        
        // Verify ownership
        if (!budget.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied: Budget does not belong to user");
        }
        
        budget.resetSpentAmount();
        budgetRepository.save(budget);
    }
    
    public List<BudgetDto> getOverBudgetBudgetsByUserId(Long userId) {
        return budgetRepository.findOverBudgetBudgetsByUserId(userId).stream()
                .map(BudgetDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    public List<BudgetDto> getBudgetsNearLimitByUserId(Long userId) {
        return budgetRepository.findBudgetsNearLimitByUserId(userId).stream()
                .map(BudgetDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    public List<BudgetDto> getOverBudgetBudgets() {
        return budgetRepository.findOverBudgetBudgets().stream()
                .map(BudgetDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    public List<BudgetDto> getBudgetsNearLimit() {
        return budgetRepository.findBudgetsNearLimit().stream()
                .map(BudgetDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    public BigDecimal getTotalBudgetAmountByUserId(Long userId) {
        return budgetRepository.getTotalBudgetAmountByUserId(userId);
    }
    
    public BigDecimal getTotalSpentAmountByUserId(Long userId) {
        return budgetRepository.getTotalSpentAmountByUserId(userId);
    }
    
    public List<BudgetDto> searchBudgetsByName(String name) {
        return budgetRepository.findByNameContainingIgnoreCase(name).stream()
                .map(BudgetDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    public long getBudgetCountByUserId(Long userId) {
        return budgetRepository.countByUserId(userId);
    }
    
    public long getActiveBudgetCountByUserId(Long userId) {
        return budgetRepository.countActiveBudgetsByUserId(userId);
    }
    
    // Internal method to get budget entity
    public Budget getBudgetEntityById(Long budgetId) {
        return budgetRepository.findById(budgetId)
                .orElseThrow(() -> new RuntimeException("Budget not found with ID: " + budgetId));
    }
} 