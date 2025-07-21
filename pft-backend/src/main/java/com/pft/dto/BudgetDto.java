package com.pft.dto;

import com.pft.entity.Budget;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BudgetDto {
    
    private Long id;
    private String name;
    private String description;
    private BigDecimal amount;
    private BigDecimal spentAmount;
    private Budget.BudgetPeriod period;
    private Budget.BudgetStatus status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean isActive;
    private Integer alertThreshold;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Related entities (simplified)
    private Long userId;
    private String userFullName;
    private Long categoryId;
    private String categoryName;
    
    // Calculated fields
    private BigDecimal remainingAmount;
    private double percentageUsed;
    private boolean isOverBudget;
    private boolean isNearLimit;
    
    public static BudgetDto fromEntity(Budget budget) {
        BudgetDto dto = new BudgetDto();
        dto.setId(budget.getId());
        dto.setName(budget.getName());
        dto.setDescription(budget.getDescription());
        dto.setAmount(budget.getAmount());
        dto.setSpentAmount(budget.getSpentAmount());
        dto.setPeriod(budget.getPeriod());
        dto.setStatus(budget.getStatus());
        dto.setStartDate(budget.getStartDate());
        dto.setEndDate(budget.getEndDate());
        dto.setActive(budget.isActive());
        dto.setAlertThreshold(budget.getAlertThreshold());
        dto.setCreatedAt(budget.getCreatedAt());
        dto.setUpdatedAt(budget.getUpdatedAt());
        
        // Calculate derived fields
        dto.setRemainingAmount(budget.getAmount().subtract(budget.getSpentAmount()));
        dto.setPercentageUsed(budget.getSpentAmount().divide(budget.getAmount(), 4, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue());
        dto.setOverBudget(budget.getSpentAmount().compareTo(budget.getAmount()) > 0);
        dto.setNearLimit(dto.getPercentageUsed() >= 80.0);
        
        // User info
        if (budget.getUser() != null) {
            dto.setUserId(budget.getUser().getId());
            dto.setUserFullName(budget.getUser().getFullName());
        }
        
        // Category info
        if (budget.getCategory() != null) {
            dto.setCategoryId(budget.getCategory().getId());
            dto.setCategoryName(budget.getCategory().getName());
        }
        
        return dto;
    }
} 