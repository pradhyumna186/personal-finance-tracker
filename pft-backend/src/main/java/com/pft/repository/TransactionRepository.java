package com.pft.repository;

import com.pft.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    /**
     * Find transactions by account ID
     */
    List<Transaction> findByAccountId(Long accountId);
    
    /**
     * Find transactions by account ID with pagination
     */
    Page<Transaction> findByAccountId(Long accountId, Pageable pageable);
    
    /**
     * Find transactions by user ID (through account)
     */
    @Query("SELECT t FROM Transaction t WHERE t.account.user.id = :userId")
    List<Transaction> findByUserId(@Param("userId") Long userId);
    
    /**
     * Find transactions by user ID with pagination
     */
    @Query("SELECT t FROM Transaction t WHERE t.account.user.id = :userId")
    Page<Transaction> findByUserId(@Param("userId") Long userId, Pageable pageable);
    
    /**
     * Find transactions by category ID
     */
    List<Transaction> findByCategoryId(Long categoryId);
    
    /**
     * Find transactions by type
     */
    List<Transaction> findByType(Transaction.TransactionType type);
    
    /**
     * Find transactions by status
     */
    List<Transaction> findByStatus(Transaction.TransactionStatus status);
    
    /**
     * Find transactions by date range
     */
    @Query("SELECT t FROM Transaction t WHERE t.transactionDate BETWEEN :startDate AND :endDate")
    List<Transaction> findByTransactionDateBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    /**
     * Find transactions by user ID and date range
     */
    @Query("SELECT t FROM Transaction t WHERE t.account.user.id = :userId AND t.transactionDate BETWEEN :startDate AND :endDate")
    List<Transaction> findByUserIdAndTransactionDateBetween(@Param("userId") Long userId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    /**
     * Find transactions by user ID and type
     */
    @Query("SELECT t FROM Transaction t WHERE t.account.user.id = :userId AND t.type = :type")
    List<Transaction> findByUserIdAndType(@Param("userId") Long userId, @Param("type") Transaction.TransactionType type);
    
    /**
     * Find transactions by user ID and category ID
     */
    @Query("SELECT t FROM Transaction t WHERE t.account.user.id = :userId AND t.category.id = :categoryId")
    List<Transaction> findByUserIdAndCategoryId(@Param("userId") Long userId, @Param("categoryId") Long categoryId);
    
    /**
     * Find recurring transactions
     */
    List<Transaction> findByIsRecurringTrue();
    
    /**
     * Find transactions by description containing (case insensitive)
     */
    List<Transaction> findByDescriptionContainingIgnoreCase(String description);
    
    /**
     * Find transactions by amount range
     */
    @Query("SELECT t FROM Transaction t WHERE t.amount BETWEEN :minAmount AND :maxAmount")
    List<Transaction> findByAmountBetween(@Param("minAmount") BigDecimal minAmount, @Param("maxAmount") BigDecimal maxAmount);
    
    /**
     * Get total amount by user ID and type
     */
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.account.user.id = :userId AND t.type = :type")
    BigDecimal getTotalAmountByUserIdAndType(@Param("userId") Long userId, @Param("type") Transaction.TransactionType type);
    
    /**
     * Get total amount by user ID and category ID
     */
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.account.user.id = :userId AND t.category.id = :categoryId")
    BigDecimal getTotalAmountByUserIdAndCategoryId(@Param("userId") Long userId, @Param("categoryId") Long categoryId);
    
    /**
     * Get total amount by user ID and date range
     */
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.account.user.id = :userId AND t.transactionDate BETWEEN :startDate AND :endDate")
    BigDecimal getTotalAmountByUserIdAndDateRange(@Param("userId") Long userId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    /**
     * Count transactions by user ID
     */
    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.account.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);
    
    /**
     * Count transactions by user ID and type
     */
    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.account.user.id = :userId AND t.type = :type")
    long countByUserIdAndType(@Param("userId") Long userId, @Param("type") Transaction.TransactionType type);
    
    /**
     * Find transactions that need to be processed for recurring
     */
    @Query("SELECT t FROM Transaction t WHERE t.isRecurring = true AND t.nextRecurringDate <= :currentDate")
    List<Transaction> findRecurringTransactionsToProcess(@Param("currentDate") LocalDateTime currentDate);
} 