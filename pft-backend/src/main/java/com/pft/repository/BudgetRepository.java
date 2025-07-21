package com.pft.repository;

import com.pft.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {
    
    /**
     * Find budgets by user ID
     */
    List<Budget> findByUserId(Long userId);
    
    /**
     * Find budgets by user ID and status
     */
    List<Budget> findByUserIdAndStatus(Long userId, Budget.BudgetStatus status);
    
    /**
     * Find budgets by user ID and period
     */
    List<Budget> findByUserIdAndPeriod(Long userId, Budget.BudgetPeriod period);
    
    /**
     * Find budgets by category ID
     */
    List<Budget> findByCategoryId(Long categoryId);
    
    /**
     * Find active budgets by user ID
     */
    @Query("SELECT b FROM Budget b WHERE b.user.id = :userId AND b.isActive = true AND b.status = 'ACTIVE'")
    List<Budget> findActiveBudgetsByUserId(@Param("userId") Long userId);
    
    /**
     * Find budgets by user ID and date range
     */
    @Query("SELECT b FROM Budget b WHERE b.user.id = :userId AND b.startDate <= :endDate AND (b.endDate IS NULL OR b.endDate >= :startDate)")
    List<Budget> findBudgetsByUserIdAndDateRange(@Param("userId") Long userId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    /**
     * Find budgets that are over budget
     */
    @Query("SELECT b FROM Budget b WHERE b.spentAmount > b.amount")
    List<Budget> findOverBudgetBudgets();
    
    /**
     * Find budgets that are near limit (80% or more spent)
     */
    @Query("SELECT b FROM Budget b WHERE (b.spentAmount / b.amount) >= 0.8")
    List<Budget> findBudgetsNearLimit();
    
    /**
     * Find budgets by user ID that are over budget
     */
    @Query("SELECT b FROM Budget b WHERE b.user.id = :userId AND b.spentAmount > b.amount")
    List<Budget> findOverBudgetBudgetsByUserId(@Param("userId") Long userId);
    
    /**
     * Find budgets by user ID that are near limit
     */
    @Query("SELECT b FROM Budget b WHERE b.user.id = :userId AND (b.spentAmount / b.amount) >= 0.8")
    List<Budget> findBudgetsNearLimitByUserId(@Param("userId") Long userId);
    
    /**
     * Get total budget amount by user ID
     */
    @Query("SELECT COALESCE(SUM(b.amount), 0) FROM Budget b WHERE b.user.id = :userId AND b.isActive = true AND b.status = 'ACTIVE'")
    BigDecimal getTotalBudgetAmountByUserId(@Param("userId") Long userId);
    
    /**
     * Get total spent amount by user ID
     */
    @Query("SELECT COALESCE(SUM(b.spentAmount), 0) FROM Budget b WHERE b.user.id = :userId AND b.isActive = true AND b.status = 'ACTIVE'")
    BigDecimal getTotalSpentAmountByUserId(@Param("userId") Long userId);
    
    /**
     * Count budgets by user ID
     */
    long countByUserId(Long userId);
    
    /**
     * Count active budgets by user ID
     */
    @Query("SELECT COUNT(b) FROM Budget b WHERE b.user.id = :userId AND b.isActive = true AND b.status = 'ACTIVE'")
    long countActiveBudgetsByUserId(@Param("userId") Long userId);
    
    /**
     * Find budgets by user ID and category ID
     */
    List<Budget> findByUserIdAndCategoryId(Long userId, Long categoryId);
    
    /**
     * Find budgets by name containing (case insensitive)
     */
    List<Budget> findByNameContainingIgnoreCase(String name);
} 