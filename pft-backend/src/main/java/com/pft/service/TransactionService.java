package com.pft.service;

import com.pft.dto.TransactionDto;
import com.pft.entity.Account;
import com.pft.entity.Category;
import com.pft.entity.Transaction;
import com.pft.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {
    
    private final TransactionRepository transactionRepository;
    private final AccountService accountService;
    private final CategoryService categoryService;
    private final UserService userService;
    
    public TransactionDto getTransactionById(Long transactionId, Long userId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found with ID: " + transactionId));
        
        // Verify ownership through account
        if (!transaction.getAccount().getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied: Transaction does not belong to user");
        }
        
        return TransactionDto.fromEntity(transaction);
    }
    
    public TransactionDto getTransactionById(Long transactionId, String userEmail) {
        Long userId = userService.getUserEntityByEmail(userEmail).getId();
        return getTransactionById(transactionId, userId);
    }
    
    public List<TransactionDto> getTransactionsByUserId(Long userId) {
        return transactionRepository.findByUserId(userId).stream()
                .map(TransactionDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    public List<TransactionDto> getTransactionsByUserEmail(String userEmail) {
        Long userId = userService.getUserEntityByEmail(userEmail).getId();
        return transactionRepository.findByUserId(userId).stream()
                .map(TransactionDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    public Page<TransactionDto> getTransactionsByUserId(Long userId, Pageable pageable) {
        return transactionRepository.findByUserId(userId, pageable)
                .map(TransactionDto::fromEntity);
    }
    
    public List<TransactionDto> getTransactionsByAccountId(Long accountId, Long userId) {
        // Verify account ownership first
        accountService.getAccountById(accountId, userId);
        
        return transactionRepository.findByAccountId(accountId).stream()
                .map(TransactionDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    public List<TransactionDto> getTransactionsByAccountId(Long accountId, String userEmail) {
        Long userId = userService.getUserEntityByEmail(userEmail).getId();
        return getTransactionsByAccountId(accountId, userId);
    }
    
    public List<TransactionDto> getTransactionsByUserIdAndType(Long userId, Transaction.TransactionType type) {
        return transactionRepository.findByUserIdAndType(userId, type).stream()
                .map(TransactionDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    public List<TransactionDto> getTransactionsByUserIdAndCategoryId(Long userId, Long categoryId) {
        return transactionRepository.findByUserIdAndCategoryId(userId, categoryId).stream()
                .map(TransactionDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    public List<TransactionDto> getTransactionsByUserEmailAndCategoryId(String userEmail, Long categoryId) {
        Long userId = userService.getUserEntityByEmail(userEmail).getId();
        return transactionRepository.findByUserIdAndCategoryId(userId, categoryId).stream()
                .map(TransactionDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    public List<TransactionDto> getTransactionsByUserIdAndDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        return transactionRepository.findByUserIdAndTransactionDateBetween(userId, startDate, endDate).stream()
                .map(TransactionDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    public List<TransactionDto> getTransactionsByUserEmailAndDateRange(String userEmail, LocalDateTime startDate, LocalDateTime endDate) {
        Long userId = userService.getUserEntityByEmail(userEmail).getId();
        return transactionRepository.findByUserIdAndTransactionDateBetween(userId, startDate, endDate).stream()
                .map(TransactionDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public TransactionDto createTransaction(TransactionDto transactionDto, Long userId) {
        // Verify account ownership
        Account account = accountService.getAccountEntityById(transactionDto.getAccountId());
        if (!account.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied: Account does not belong to user");
        }
        
        // Verify category ownership if provided
        Category category = null;
        if (transactionDto.getCategoryId() != null) {
            category = categoryService.getCategoryEntityById(transactionDto.getCategoryId());
            if (!category.getUser().getId().equals(userId)) {
                throw new RuntimeException("Access denied: Category does not belong to user");
            }
        }
        
        // Verify toAccount ownership for transfers
        Account toAccount = null;
        if (transactionDto.getToAccountId() != null) {
            toAccount = accountService.getAccountEntityById(transactionDto.getToAccountId());
            if (!toAccount.getUser().getId().equals(userId)) {
                throw new RuntimeException("Access denied: To account does not belong to user");
            }
        }
        
        // Check sufficient funds for expenses and transfers
        if (transactionDto.getType() == Transaction.TransactionType.EXPENSE || 
            transactionDto.getType() == Transaction.TransactionType.TRANSFER) {
            if (!account.hasSufficientFunds(transactionDto.getAmount())) {
                throw new RuntimeException("Insufficient funds in account");
            }
        }
        
        Transaction transaction = new Transaction();
        transaction.setAmount(transactionDto.getAmount());
        transaction.setDescription(transactionDto.getDescription());
        transaction.setType(transactionDto.getType());
        transaction.setTransactionDate(transactionDto.getTransactionDate() != null ? 
                transactionDto.getTransactionDate() : LocalDateTime.now());
        transaction.setReferenceNumber(transactionDto.getReferenceNumber());
        transaction.setNotes(transactionDto.getNotes());
        transaction.setRecurring(transactionDto.isRecurring());
        transaction.setRecurringFrequency(transactionDto.getRecurringFrequency());
        transaction.setNextRecurringDate(transactionDto.getNextRecurringDate());
        transaction.setAccount(account);
        transaction.setCategory(category);
        transaction.setToAccount(toAccount);
        
        Transaction savedTransaction = transactionRepository.save(transaction);
        
        // Update account balance
        updateAccountBalance(transaction);
        
        return TransactionDto.fromEntity(savedTransaction);
    }
    
    @Transactional
    public TransactionDto createTransaction(TransactionDto transactionDto, String userEmail) {
        Long userId = userService.getUserEntityByEmail(userEmail).getId();
        return createTransaction(transactionDto, userId);
    }
    
    @Transactional
    public TransactionDto updateTransaction(Long transactionId, TransactionDto transactionDto, Long userId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found with ID: " + transactionId));
        
        // Verify ownership
        if (!transaction.getAccount().getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied: Transaction does not belong to user");
        }
        
        // Store old amount for balance adjustment
        BigDecimal oldAmount = transaction.getAmount();
        
        // Update fields
        if (transactionDto.getDescription() != null) {
            transaction.setDescription(transactionDto.getDescription());
        }
        if (transactionDto.getAmount() != null) {
            transaction.setAmount(transactionDto.getAmount());
        }
        if (transactionDto.getReferenceNumber() != null) {
            transaction.setReferenceNumber(transactionDto.getReferenceNumber());
        }
        if (transactionDto.getNotes() != null) {
            transaction.setNotes(transactionDto.getNotes());
        }
        if (transactionDto.getStatus() != null) {
            transaction.setStatus(transactionDto.getStatus());
        }
        
        Transaction updatedTransaction = transactionRepository.save(transaction);
        
        // Update account balance if amount changed
        if (oldAmount.compareTo(transactionDto.getAmount()) != 0) {
            // Reverse old amount
            reverseAccountBalance(transaction, oldAmount);
            // Apply new amount
            updateAccountBalance(updatedTransaction);
        }
        
        return TransactionDto.fromEntity(updatedTransaction);
    }
    
    @Transactional
    public TransactionDto updateTransaction(Long transactionId, TransactionDto transactionDto, String userEmail) {
        Long userId = userService.getUserEntityByEmail(userEmail).getId();
        return updateTransaction(transactionId, transactionDto, userId);
    }
    
    @Transactional
    public void deleteTransaction(Long transactionId, Long userId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found with ID: " + transactionId));
        
        // Verify ownership
        if (!transaction.getAccount().getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied: Transaction does not belong to user");
        }
        
        // Reverse account balance
        reverseAccountBalance(transaction, transaction.getAmount());
        
        transactionRepository.delete(transaction);
    }
    
    @Transactional
    public void deleteTransaction(Long transactionId, String userEmail) {
        Long userId = userService.getUserEntityByEmail(userEmail).getId();
        deleteTransaction(transactionId, userId);
    }
    
    private void updateAccountBalance(Transaction transaction) {
        BigDecimal amount = transaction.getSignedAmount();
        accountService.updateAccountBalance(transaction.getAccount().getId(), amount);
        
        // For transfers, also update the destination account
        if (transaction.getType() == Transaction.TransactionType.TRANSFER && transaction.getToAccount() != null) {
            accountService.updateAccountBalance(transaction.getToAccount().getId(), transaction.getAmount());
        }
    }
    
    private void reverseAccountBalance(Transaction transaction, BigDecimal amount) {
        BigDecimal reverseAmount = amount.negate();
        if (transaction.getType() == Transaction.TransactionType.EXPENSE) {
            reverseAmount = amount; // Reverse expense (add back)
        } else if (transaction.getType() == Transaction.TransactionType.INCOME) {
            reverseAmount = amount.negate(); // Reverse income (subtract)
        }
        
        accountService.updateAccountBalance(transaction.getAccount().getId(), reverseAmount);
        
        // For transfers, also reverse the destination account
        if (transaction.getType() == Transaction.TransactionType.TRANSFER && transaction.getToAccount() != null) {
            accountService.updateAccountBalance(transaction.getToAccount().getId(), amount.negate());
        }
    }
    
    public BigDecimal getTotalAmountByUserIdAndType(Long userId, Transaction.TransactionType type) {
        return transactionRepository.getTotalAmountByUserIdAndType(userId, type);
    }
    
    public BigDecimal getTotalAmountByUserIdAndCategoryId(Long userId, Long categoryId) {
        return transactionRepository.getTotalAmountByUserIdAndCategoryId(userId, categoryId);
    }
    
    public BigDecimal getTotalAmountByUserIdAndDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        return transactionRepository.getTotalAmountByUserIdAndDateRange(userId, startDate, endDate);
    }
    
    public List<TransactionDto> getRecurringTransactions() {
        return transactionRepository.findByIsRecurringTrue().stream()
                .map(TransactionDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    public List<TransactionDto> searchTransactionsByDescription(String description) {
        return transactionRepository.findByDescriptionContainingIgnoreCase(description).stream()
                .map(TransactionDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    public long getTransactionCountByUserId(Long userId) {
        return transactionRepository.countByUserId(userId);
    }
    
    public long getTransactionCountByUserEmail(String userEmail) {
        Long userId = userService.getUserEntityByEmail(userEmail).getId();
        return transactionRepository.countByUserId(userId);
    }
    
    public long getTransactionCountByUserIdAndType(Long userId, Transaction.TransactionType type) {
        return transactionRepository.countByUserIdAndType(userId, type);
    }
    
    // Internal method to get transaction entity
    public Transaction getTransactionEntityById(Long transactionId) {
        return transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found with ID: " + transactionId));
    }
} 