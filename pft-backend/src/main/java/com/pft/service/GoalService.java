package com.pft.service;

import com.pft.dto.GoalDto;
import com.pft.entity.Goal;
import com.pft.entity.User;
import com.pft.repository.GoalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GoalService {
    
    private final GoalRepository goalRepository;
    private final UserService userService;
    
    public GoalDto getGoalById(Long goalId, Long userId) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new RuntimeException("Goal not found with ID: " + goalId));
        
        // Verify ownership
        if (!goal.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied: Goal does not belong to user");
        }
        
        return GoalDto.fromEntity(goal);
    }
    
    public List<GoalDto> getGoalsByUserId(Long userId) {
        return goalRepository.findByUserId(userId).stream()
                .map(GoalDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    public List<GoalDto> getActiveGoalsByUserId(Long userId) {
        return goalRepository.findActiveGoalsByUserId(userId).stream()
                .map(GoalDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    public List<GoalDto> getCompletedGoalsByUserId(Long userId) {
        return goalRepository.findCompletedGoalsByUserId(userId).stream()
                .map(GoalDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    public List<GoalDto> getGoalsByUserIdAndType(Long userId, Goal.GoalType type) {
        return goalRepository.findByUserIdAndType(userId, type).stream()
                .map(GoalDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    public List<GoalDto> getGoalsByUserIdAndTargetDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        return goalRepository.findByUserIdAndTargetDateBetween(userId, startDate, endDate).stream()
                .map(GoalDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public GoalDto createGoal(GoalDto goalDto, Long userId) {
        User user = userService.getUserEntityById(userId);
        
        Goal goal = new Goal();
        goal.setName(goalDto.getName());
        goal.setDescription(goalDto.getDescription());
        goal.setTargetAmount(goalDto.getTargetAmount());
        goal.setCurrentAmount(goalDto.getCurrentAmount() != null ? goalDto.getCurrentAmount() : BigDecimal.ZERO);
        goal.setType(goalDto.getType());
        goal.setTargetDate(goalDto.getTargetDate());
        goal.setColor(goalDto.getColor());
        goal.setIcon(goalDto.getIcon());
        goal.setUser(user);
        
        Goal savedGoal = goalRepository.save(goal);
        return GoalDto.fromEntity(savedGoal);
    }
    
    @Transactional
    public GoalDto updateGoal(Long goalId, GoalDto goalDto, Long userId) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new RuntimeException("Goal not found with ID: " + goalId));
        
        // Verify ownership
        if (!goal.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied: Goal does not belong to user");
        }
        
        // Update fields
        if (goalDto.getName() != null) {
            goal.setName(goalDto.getName());
        }
        if (goalDto.getDescription() != null) {
            goal.setDescription(goalDto.getDescription());
        }
        if (goalDto.getTargetAmount() != null) {
            goal.setTargetAmount(goalDto.getTargetAmount());
        }
        if (goalDto.getCurrentAmount() != null) {
            goal.setCurrentAmount(goalDto.getCurrentAmount());
        }
        if (goalDto.getTargetDate() != null) {
            goal.setTargetDate(goalDto.getTargetDate());
        }
        if (goalDto.getColor() != null) {
            goal.setColor(goalDto.getColor());
        }
        if (goalDto.getIcon() != null) {
            goal.setIcon(goalDto.getIcon());
        }
        if (goalDto.getStatus() != null) {
            goal.setStatus(goalDto.getStatus());
        }
        
        Goal updatedGoal = goalRepository.save(goal);
        return GoalDto.fromEntity(updatedGoal);
    }
    
    @Transactional
    public void deleteGoal(Long goalId, Long userId) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new RuntimeException("Goal not found with ID: " + goalId));
        
        // Verify ownership
        if (!goal.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied: Goal does not belong to user");
        }
        
        goalRepository.delete(goal);
    }
    
    @Transactional
    public GoalDto addProgress(Long goalId, BigDecimal amount, Long userId) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new RuntimeException("Goal not found with ID: " + goalId));
        
        // Verify ownership
        if (!goal.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied: Goal does not belong to user");
        }
        
        // Add progress
        BigDecimal newCurrentAmount = goal.getCurrentAmount().add(amount);
        goal.setCurrentAmount(newCurrentAmount);
        
        // Check if goal is completed
        if (newCurrentAmount.compareTo(goal.getTargetAmount()) >= 0) {
            goal.setStatus(Goal.GoalStatus.COMPLETED);
        }
        
        Goal updatedGoal = goalRepository.save(goal);
        return GoalDto.fromEntity(updatedGoal);
    }
    
    @Transactional
    public GoalDto setProgress(Long goalId, BigDecimal amount, Long userId) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new RuntimeException("Goal not found with ID: " + goalId));
        
        // Verify ownership
        if (!goal.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied: Goal does not belong to user");
        }
        
        // Set progress
        goal.setCurrentAmount(amount);
        
        // Check if goal is completed
        if (amount.compareTo(goal.getTargetAmount()) >= 0) {
            goal.setStatus(Goal.GoalStatus.COMPLETED);
        }
        
        Goal updatedGoal = goalRepository.save(goal);
        return GoalDto.fromEntity(updatedGoal);
    }
    
    public List<GoalDto> getGoalsDueSoonByUserId(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime thirtyDaysFromNow = now.plusDays(30);
        return goalRepository.findGoalsDueSoonByUserId(userId, now, thirtyDaysFromNow).stream()
                .map(GoalDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    public List<GoalDto> getGoalsNearCompletionByUserId(Long userId) {
        return goalRepository.findGoalsNearCompletionByUserId(userId).stream()
                .map(GoalDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    public List<GoalDto> getOverdueGoalsByUserId(Long userId) {
        return goalRepository.findOverdueGoalsByUserId(userId, LocalDateTime.now()).stream()
                .map(GoalDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    public List<GoalDto> getGoalsDueSoon() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime thirtyDaysFromNow = now.plusDays(30);
        return goalRepository.findGoalsDueSoon(now, thirtyDaysFromNow).stream()
                .map(GoalDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    public List<GoalDto> getGoalsNearCompletion() {
        return goalRepository.findGoalsNearCompletion().stream()
                .map(GoalDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    public List<GoalDto> getOverdueGoals() {
        return goalRepository.findOverdueGoals(LocalDateTime.now()).stream()
                .map(GoalDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    public BigDecimal getTotalTargetAmountByUserId(Long userId) {
        return goalRepository.getTotalTargetAmountByUserId(userId);
    }
    
    public BigDecimal getTotalCurrentAmountByUserId(Long userId) {
        return goalRepository.getTotalCurrentAmountByUserId(userId);
    }
    
    public List<GoalDto> searchGoalsByName(String name) {
        return goalRepository.findByNameContainingIgnoreCase(name).stream()
                .map(GoalDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    public long getGoalCountByUserId(Long userId) {
        return goalRepository.countByUserId(userId);
    }
    
    public long getActiveGoalCountByUserId(Long userId) {
        return goalRepository.countActiveGoalsByUserId(userId);
    }
    
    public long getCompletedGoalCountByUserId(Long userId) {
        return goalRepository.countCompletedGoalsByUserId(userId);
    }
    
    // Internal method to get goal entity
    public Goal getGoalEntityById(Long goalId) {
        return goalRepository.findById(goalId)
                .orElseThrow(() -> new RuntimeException("Goal not found with ID: " + goalId));
    }
} 