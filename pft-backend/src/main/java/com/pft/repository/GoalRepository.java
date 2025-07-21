package com.pft.repository;

import com.pft.entity.Goal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {
    
    /**
     * Find goals by user ID
     */
    List<Goal> findByUserId(Long userId);
    
    /**
     * Find goals by user ID and status
     */
    List<Goal> findByUserIdAndStatus(Long userId, Goal.GoalStatus status);
    
    /**
     * Find goals by user ID and type
     */
    List<Goal> findByUserIdAndType(Long userId, Goal.GoalType type);
    
    /**
     * Find active goals by user ID
     */
    @Query("SELECT g FROM Goal g WHERE g.user.id = :userId AND g.status = 'ACTIVE'")
    List<Goal> findActiveGoalsByUserId(@Param("userId") Long userId);
    
    /**
     * Find completed goals by user ID
     */
    @Query("SELECT g FROM Goal g WHERE g.user.id = :userId AND g.status = 'COMPLETED'")
    List<Goal> findCompletedGoalsByUserId(@Param("userId") Long userId);
    
    /**
     * Find goals by name containing (case insensitive)
     */
    List<Goal> findByNameContainingIgnoreCase(String name);
    
    /**
     * Find goals by target date range
     */
    @Query("SELECT g FROM Goal g WHERE g.targetDate BETWEEN :startDate AND :endDate")
    List<Goal> findByTargetDateBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    /**
     * Find goals by user ID and target date range
     */
    @Query("SELECT g FROM Goal g WHERE g.user.id = :userId AND g.targetDate BETWEEN :startDate AND :endDate")
    List<Goal> findByUserIdAndTargetDateBetween(@Param("userId") Long userId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    /**
     * Find goals that are due soon (within 30 days)
     */
    @Query("SELECT g FROM Goal g WHERE g.targetDate BETWEEN :now AND :thirtyDaysFromNow AND g.status = 'ACTIVE'")
    List<Goal> findGoalsDueSoon(@Param("now") LocalDateTime now, @Param("thirtyDaysFromNow") LocalDateTime thirtyDaysFromNow);
    
    /**
     * Find goals by user ID that are due soon
     */
    @Query("SELECT g FROM Goal g WHERE g.user.id = :userId AND g.targetDate BETWEEN :now AND :thirtyDaysFromNow AND g.status = 'ACTIVE'")
    List<Goal> findGoalsDueSoonByUserId(@Param("userId") Long userId, @Param("now") LocalDateTime now, @Param("thirtyDaysFromNow") LocalDateTime thirtyDaysFromNow);
    
    /**
     * Find goals that are over 80% complete
     */
    @Query("SELECT g FROM Goal g WHERE (g.currentAmount / g.targetAmount) >= 0.8 AND g.status = 'ACTIVE'")
    List<Goal> findGoalsNearCompletion();
    
    /**
     * Find goals by user ID that are near completion
     */
    @Query("SELECT g FROM Goal g WHERE g.user.id = :userId AND (g.currentAmount / g.targetAmount) >= 0.8 AND g.status = 'ACTIVE'")
    List<Goal> findGoalsNearCompletionByUserId(@Param("userId") Long userId);
    
    /**
     * Find overdue goals
     */
    @Query("SELECT g FROM Goal g WHERE g.targetDate < :now AND g.status = 'ACTIVE'")
    List<Goal> findOverdueGoals(@Param("now") LocalDateTime now);
    
    /**
     * Find overdue goals by user ID
     */
    @Query("SELECT g FROM Goal g WHERE g.user.id = :userId AND g.targetDate < :now AND g.status = 'ACTIVE'")
    List<Goal> findOverdueGoalsByUserId(@Param("userId") Long userId, @Param("now") LocalDateTime now);
    
    /**
     * Get total target amount by user ID
     */
    @Query("SELECT COALESCE(SUM(g.targetAmount), 0) FROM Goal g WHERE g.user.id = :userId AND g.status = 'ACTIVE'")
    BigDecimal getTotalTargetAmountByUserId(@Param("userId") Long userId);
    
    /**
     * Get total current amount by user ID
     */
    @Query("SELECT COALESCE(SUM(g.currentAmount), 0) FROM Goal g WHERE g.user.id = :userId AND g.status = 'ACTIVE'")
    BigDecimal getTotalCurrentAmountByUserId(@Param("userId") Long userId);
    
    /**
     * Count goals by user ID
     */
    long countByUserId(Long userId);
    
    /**
     * Count active goals by user ID
     */
    @Query("SELECT COUNT(g) FROM Goal g WHERE g.user.id = :userId AND g.status = 'ACTIVE'")
    long countActiveGoalsByUserId(@Param("userId") Long userId);
    
    /**
     * Count completed goals by user ID
     */
    @Query("SELECT COUNT(g) FROM Goal g WHERE g.user.id = :userId AND g.status = 'COMPLETED'")
    long countCompletedGoalsByUserId(@Param("userId") Long userId);
} 