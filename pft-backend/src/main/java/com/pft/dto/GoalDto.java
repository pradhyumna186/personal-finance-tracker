package com.pft.dto;

import com.pft.entity.Goal;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class GoalDto {
    
    private Long id;
    private String name;
    private String description;
    private BigDecimal targetAmount;
    private BigDecimal currentAmount;
    private Goal.GoalType type;
    private Goal.GoalStatus status;
    private LocalDateTime targetDate;
    private String color;
    private String icon;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Related entities (simplified)
    private Long userId;
    private String userFullName;
    
    // Calculated fields
    private BigDecimal remainingAmount;
    private double percentageComplete;
    private boolean isCompleted;
    private boolean isOverdue;
    private boolean isNearCompletion;
    private long daysRemaining;
    
    public static GoalDto fromEntity(Goal goal) {
        GoalDto dto = new GoalDto();
        dto.setId(goal.getId());
        dto.setName(goal.getName());
        dto.setDescription(goal.getDescription());
        dto.setTargetAmount(goal.getTargetAmount());
        dto.setCurrentAmount(goal.getCurrentAmount());
        dto.setType(goal.getType());
        dto.setStatus(goal.getStatus());
        dto.setTargetDate(goal.getTargetDate());
        dto.setColor(goal.getColor());
        dto.setIcon(goal.getIcon());
        dto.setCreatedAt(goal.getCreatedAt());
        dto.setUpdatedAt(goal.getUpdatedAt());
        
        // Calculate derived fields
        dto.setRemainingAmount(goal.getTargetAmount().subtract(goal.getCurrentAmount()));
        dto.setPercentageComplete(goal.getCurrentAmount().divide(goal.getTargetAmount(), 4, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue());
        dto.setCompleted(goal.getStatus() == Goal.GoalStatus.COMPLETED);
        dto.setOverdue(goal.getTargetDate().isBefore(LocalDateTime.now()) && goal.getStatus() == Goal.GoalStatus.ACTIVE);
        dto.setNearCompletion(dto.getPercentageComplete() >= 80.0);
        
        // Calculate days remaining
        if (goal.getTargetDate() != null && goal.getStatus() == Goal.GoalStatus.ACTIVE) {
            dto.setDaysRemaining(java.time.Duration.between(LocalDateTime.now(), goal.getTargetDate()).toDays());
        }
        
        // User info
        if (goal.getUser() != null) {
            dto.setUserId(goal.getUser().getId());
            dto.setUserFullName(goal.getUser().getFullName());
        }
        
        return dto;
    }
} 