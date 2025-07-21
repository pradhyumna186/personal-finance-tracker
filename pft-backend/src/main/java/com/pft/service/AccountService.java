package com.pft.service;

import com.pft.dto.AccountDto;
import com.pft.entity.Account;
import com.pft.entity.User;
import com.pft.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountService {
    
    private final AccountRepository accountRepository;
    private final UserService userService;
    
    public AccountDto getAccountById(Long accountId, Long userId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found with ID: " + accountId));
        
        // Verify ownership
        if (!account.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied: Account does not belong to user");
        }
        
        return AccountDto.fromEntity(account);
    }
    
    public List<AccountDto> getAccountsByUserId(Long userId) {
        return accountRepository.findByUserId(userId).stream()
                .map(AccountDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    public List<AccountDto> getActiveAccountsByUserId(Long userId) {
        return accountRepository.findActiveAccountsByUserId(userId).stream()
                .map(AccountDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public AccountDto createAccount(AccountDto accountDto, Long userId) {
        User user = userService.getUserEntityById(userId);
        
        Account account = new Account();
        account.setName(accountDto.getName());
        account.setType(accountDto.getType());
        account.setInitialBalance(accountDto.getInitialBalance());
        account.setCurrentBalance(accountDto.getInitialBalance());
        account.setAccountNumber(accountDto.getAccountNumber());
        account.setInstitutionName(accountDto.getInstitutionName());
        account.setColor(accountDto.getColor());
        account.setIcon(accountDto.getIcon());
        account.setUser(user);
        
        // If this is the first account, make it default
        if (accountRepository.countByUserId(userId) == 0) {
            account.setDefault(true);
        }
        
        Account savedAccount = accountRepository.save(account);
        return AccountDto.fromEntity(savedAccount);
    }
    
    @Transactional
    public AccountDto updateAccount(Long accountId, AccountDto accountDto, Long userId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found with ID: " + accountId));
        
        // Verify ownership
        if (!account.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied: Account does not belong to user");
        }
        
        // Update fields
        if (accountDto.getName() != null) {
            account.setName(accountDto.getName());
        }
        if (accountDto.getAccountNumber() != null) {
            account.setAccountNumber(accountDto.getAccountNumber());
        }
        if (accountDto.getInstitutionName() != null) {
            account.setInstitutionName(accountDto.getInstitutionName());
        }
        if (accountDto.getColor() != null) {
            account.setColor(accountDto.getColor());
        }
        if (accountDto.getIcon() != null) {
            account.setIcon(accountDto.getIcon());
        }
        if (accountDto.getStatus() != null) {
            account.setStatus(accountDto.getStatus());
        }
        
        Account updatedAccount = accountRepository.save(account);
        return AccountDto.fromEntity(updatedAccount);
    }
    
    @Transactional
    public void deleteAccount(Long accountId, Long userId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found with ID: " + accountId));
        
        // Verify ownership
        if (!account.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied: Account does not belong to user");
        }
        
        // Check if account has transactions
        if (!account.getTransactions().isEmpty()) {
            throw new RuntimeException("Cannot delete account with existing transactions");
        }
        
        accountRepository.delete(account);
    }
    
    @Transactional
    public AccountDto setDefaultAccount(Long accountId, Long userId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found with ID: " + accountId));
        
        // Verify ownership
        if (!account.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied: Account does not belong to user");
        }
        
        // Remove default from all other accounts
        List<Account> userAccounts = accountRepository.findByUserId(userId);
        for (Account userAccount : userAccounts) {
            if (userAccount.isDefault()) {
                userAccount.setDefault(false);
                accountRepository.save(userAccount);
            }
        }
        
        // Set this account as default
        account.setDefault(true);
        Account updatedAccount = accountRepository.save(account);
        return AccountDto.fromEntity(updatedAccount);
    }
    
    @Transactional
    public void updateAccountBalance(Long accountId, BigDecimal amount) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found with ID: " + accountId));
        
        account.updateBalance(amount);
        accountRepository.save(account);
    }
    
    public BigDecimal getTotalBalanceByUserId(Long userId) {
        return accountRepository.getTotalBalanceByUserId(userId);
    }
    
    public List<AccountDto> getAccountsWithBalanceGreaterThan(BigDecimal amount) {
        return accountRepository.findAccountsWithBalanceGreaterThan(amount).stream()
                .map(AccountDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    public List<AccountDto> getAccountsWithBalanceLessThan(BigDecimal amount) {
        return accountRepository.findAccountsWithBalanceLessThan(amount).stream()
                .map(AccountDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    public AccountDto getDefaultAccountByUserId(Long userId) {
        return accountRepository.findByUserIdAndIsDefaultTrue(userId)
                .map(AccountDto::fromEntity)
                .orElseThrow(() -> new RuntimeException("No default account found for user"));
    }
    
    public long getAccountCountByUserId(Long userId) {
        return accountRepository.countByUserId(userId);
    }
    
    public long getAccountCountByUserIdAndType(Long userId, Account.AccountType type) {
        return accountRepository.countByUserIdAndType(userId, type);
    }
    
    // Internal method to get account entity
    public Account getAccountEntityById(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found with ID: " + accountId));
    }
} 