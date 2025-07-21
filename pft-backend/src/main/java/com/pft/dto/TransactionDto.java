package com.pft.dto;

import com.pft.entity.Transaction;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionDto {
    
    private Long id;
    private String description;
    private BigDecimal amount;
    private Transaction.TransactionType type;
    private Transaction.TransactionStatus status;
    private LocalDateTime transactionDate;
    private String referenceNumber;
    private String notes;
    private boolean isRecurring;
    private Transaction.RecurringFrequency recurringFrequency;
    private LocalDateTime nextRecurringDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Related entities (simplified)
    private Long accountId;
    private String accountName;
    private Long categoryId;
    private String categoryName;
    private Long toAccountId; // For transfers
    private String toAccountName; // For transfers
    private Long userId;
    private String userFullName;
    
    public static TransactionDto fromEntity(Transaction transaction) {
        TransactionDto dto = new TransactionDto();
        dto.setId(transaction.getId());
        dto.setDescription(transaction.getDescription());
        dto.setAmount(transaction.getAmount());
        dto.setType(transaction.getType());
        dto.setStatus(transaction.getStatus());
        dto.setTransactionDate(transaction.getTransactionDate());
        dto.setReferenceNumber(transaction.getReferenceNumber());
        dto.setNotes(transaction.getNotes());
        dto.setRecurring(transaction.isRecurring());
        dto.setRecurringFrequency(transaction.getRecurringFrequency());
        dto.setNextRecurringDate(transaction.getNextRecurringDate());
        dto.setCreatedAt(transaction.getCreatedAt());
        dto.setUpdatedAt(transaction.getUpdatedAt());
        
        // Account info
        if (transaction.getAccount() != null) {
            dto.setAccountId(transaction.getAccount().getId());
            dto.setAccountName(transaction.getAccount().getName());
        }
        
        // Category info
        if (transaction.getCategory() != null) {
            dto.setCategoryId(transaction.getCategory().getId());
            dto.setCategoryName(transaction.getCategory().getName());
        }
        
        // To Account info (for transfers)
        if (transaction.getToAccount() != null) {
            dto.setToAccountId(transaction.getToAccount().getId());
            dto.setToAccountName(transaction.getToAccount().getName());
        }
        
        // User info
        if (transaction.getAccount() != null && transaction.getAccount().getUser() != null) {
            dto.setUserId(transaction.getAccount().getUser().getId());
            dto.setUserFullName(transaction.getAccount().getUser().getFullName());
        }
        
        return dto;
    }
} 