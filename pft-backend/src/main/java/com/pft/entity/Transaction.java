package com.pft.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@EntityListeners(AuditingEntityListener.class)
public class Transaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "Amount is required")
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;
    
    @Column(name = "description")
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private TransactionType type;
    
    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;
    
    @Column(name = "reference_number")
    private String referenceNumber;
    
    @Column(name = "notes")
    private String notes;
    
    @Column(name = "is_recurring")
    private boolean isRecurring = false;
    
    @Column(name = "recurring_frequency")
    @Enumerated(EnumType.STRING)
    private RecurringFrequency recurringFrequency;
    
    @Column(name = "next_recurring_date")
    private LocalDateTime nextRecurringDate;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status = TransactionStatus.COMPLETED;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_account_id")
    private Account toAccount; // For transfers between accounts
    
    // Constructors
    public Transaction() {}
    
    public Transaction(BigDecimal amount, String description, TransactionType type, 
                      LocalDateTime transactionDate, Account account) {
        this.amount = amount;
        this.description = description;
        this.type = type;
        this.transactionDate = transactionDate;
        this.account = account;
    }
    
    public Transaction(BigDecimal amount, String description, TransactionType type, 
                      LocalDateTime transactionDate, Account account, Category category) {
        this.amount = amount;
        this.description = description;
        this.type = type;
        this.transactionDate = transactionDate;
        this.account = account;
        this.category = category;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public TransactionType getType() {
        return type;
    }
    
    public void setType(TransactionType type) {
        this.type = type;
    }
    
    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }
    
    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }
    
    public String getReferenceNumber() {
        return referenceNumber;
    }
    
    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public boolean isRecurring() {
        return isRecurring;
    }
    
    public void setRecurring(boolean recurring) {
        isRecurring = recurring;
    }
    
    public RecurringFrequency getRecurringFrequency() {
        return recurringFrequency;
    }
    
    public void setRecurringFrequency(RecurringFrequency recurringFrequency) {
        this.recurringFrequency = recurringFrequency;
    }
    
    public LocalDateTime getNextRecurringDate() {
        return nextRecurringDate;
    }
    
    public void setNextRecurringDate(LocalDateTime nextRecurringDate) {
        this.nextRecurringDate = nextRecurringDate;
    }
    
    public TransactionStatus getStatus() {
        return status;
    }
    
    public void setStatus(TransactionStatus status) {
        this.status = status;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public Account getAccount() {
        return account;
    }
    
    public void setAccount(Account account) {
        this.account = account;
    }
    
    public Category getCategory() {
        return category;
    }
    
    public void setCategory(Category category) {
        this.category = category;
    }
    
    public Account getToAccount() {
        return toAccount;
    }
    
    public void setToAccount(Account toAccount) {
        this.toAccount = toAccount;
    }
    
    // Helper methods
    public boolean isIncome() {
        return type == TransactionType.INCOME;
    }
    
    public boolean isExpense() {
        return type == TransactionType.EXPENSE;
    }
    
    public boolean isTransfer() {
        return type == TransactionType.TRANSFER;
    }
    
    public BigDecimal getSignedAmount() {
        if (isIncome()) {
            return amount.abs();
        } else if (isExpense()) {
            return amount.abs().negate();
        } else {
            return amount; // Transfer amount as is
        }
    }
    
    public String getFormattedAmount() {
        return String.format("$%.2f", amount.abs());
    }
    
    public String getAccountName() {
        return account != null ? account.getName() : "Unknown Account";
    }
    
    public String getCategoryName() {
        return category != null ? category.getName() : "Uncategorized";
    }
    
    public String getToAccountName() {
        return toAccount != null ? toAccount.getName() : null;
    }
    
    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                ", type=" + type +
                ", transactionDate=" + transactionDate +
                ", account=" + (account != null ? account.getName() : "null") +
                ", category=" + (category != null ? category.getName() : "null") +
                ", status=" + status +
                '}';
    }
    
    // Transaction Type Enum
    public enum TransactionType {
        INCOME("Income"),
        EXPENSE("Expense"),
        TRANSFER("Transfer"),
        ADJUSTMENT("Adjustment");
        
        private final String displayName;
        
        TransactionType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    // Transaction Status Enum
    public enum TransactionStatus {
        PENDING("Pending"),
        COMPLETED("Completed"),
        CANCELLED("Cancelled"),
        FAILED("Failed");
        
        private final String displayName;
        
        TransactionStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    // Recurring Frequency Enum
    public enum RecurringFrequency {
        DAILY("Daily"),
        WEEKLY("Weekly"),
        MONTHLY("Monthly"),
        YEARLY("Yearly");
        
        private final String displayName;
        
        RecurringFrequency(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
} 