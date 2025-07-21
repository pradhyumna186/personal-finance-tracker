package com.pft.repository;

import com.pft.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    
    /**
     * Find accounts by user ID
     */
    List<Account> findByUserId(Long userId);
    
    /**
     * Find accounts by user ID and status
     */
    List<Account> findByUserIdAndStatus(Long userId, Account.AccountStatus status);
    
    /**
     * Find accounts by type
     */
    List<Account> findByType(Account.AccountType type);
    
    /**
     * Find default account for a user
     */
    Optional<Account> findByUserIdAndIsDefaultTrue(Long userId);
    
    /**
     * Find accounts by institution name
     */
    List<Account> findByInstitutionNameContainingIgnoreCase(String institutionName);
    
    /**
     * Find accounts with balance greater than specified amount
     */
    @Query("SELECT a FROM Account a WHERE a.currentBalance > :amount")
    List<Account> findAccountsWithBalanceGreaterThan(@Param("amount") BigDecimal amount);
    
    /**
     * Find accounts with balance less than specified amount
     */
    @Query("SELECT a FROM Account a WHERE a.currentBalance < :amount")
    List<Account> findAccountsWithBalanceLessThan(@Param("amount") BigDecimal amount);
    
    /**
     * Get total balance for a user
     */
    @Query("SELECT COALESCE(SUM(a.currentBalance), 0) FROM Account a WHERE a.user.id = :userId AND a.status = 'ACTIVE'")
    BigDecimal getTotalBalanceByUserId(@Param("userId") Long userId);
    
    /**
     * Count accounts by user ID
     */
    long countByUserId(Long userId);
    
    /**
     * Count accounts by user ID and type
     */
    long countByUserIdAndType(Long userId, Account.AccountType type);
    
    /**
     * Find active accounts by user ID
     */
    @Query("SELECT a FROM Account a WHERE a.user.id = :userId AND a.status = 'ACTIVE'")
    List<Account> findActiveAccountsByUserId(@Param("userId") Long userId);
} 