package com.pft.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "budgets")
@EntityListeners(AuditingEntityListener.class)
public class Budget {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Budget name is required")
    @Column(name = "name", nullable = false)
    private String name;
    
    @NotNull(message = "Budget amount is required")
    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;
    
    @Column(name = "spent_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal spentAmount = BigDecimal.ZERO;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "period", nullable = false)
    private BudgetPeriod period;
    
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;
    
    @Column(name = "end_date")
    private LocalDateTime endDate;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "color", length = 7)
    private String color = "#10B981"; // Default green color
    
    @Column(name = "alert_threshold")
    private Integer alertThreshold = 80; // Percentage
    
    @Column(name = "is_active")
    private boolean isActive = true;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BudgetStatus status = BudgetStatus.ACTIVE;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
    
    // Constructors
    public Budget() {}
    
    public Budget(String name, BigDecimal amount, BudgetPeriod period, 
                  LocalDateTime startDate, User user) {
        this.name = name;
        this.amount = amount;
        this.period = period;
        this.startDate = startDate;
        this.user = user;
    }
    
    public Budget(String name, BigDecimal amount, BudgetPeriod period, 
                  LocalDateTime startDate, User user, Category category) {
        this.name = name;
        this.amount = amount;
        this.period = period;
        this.startDate = startDate;
        this.user = user;
        this.category = category;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public BigDecimal getSpentAmount() {
        return spentAmount;
    }
    
    public void setSpentAmount(BigDecimal spentAmount) {
        this.spentAmount = spentAmount;
    }
    
    public BudgetPeriod getPeriod() {
        return period;
    }
    
    public void setPeriod(BudgetPeriod period) {
        this.period = period;
    }
    
    public LocalDateTime getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }
    
    public LocalDateTime getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getColor() {
        return color;
    }
    
    public void setColor(String color) {
        this.color = color;
    }
    
    public Integer getAlertThreshold() {
        return alertThreshold;
    }
    
    public void setAlertThreshold(Integer alertThreshold) {
        this.alertThreshold = alertThreshold;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        isActive = active;
    }
    
    public BudgetStatus getStatus() {
        return status;
    }
    
    public void setStatus(BudgetStatus status) {
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
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public Category getCategory() {
        return category;
    }
    
    public void setCategory(Category category) {
        this.category = category;
    }
    
    // Helper methods
    public BigDecimal getRemainingAmount() {
        return amount.subtract(spentAmount);
    }
    
    public BigDecimal getSpentPercentage() {
        if (amount.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return spentAmount.divide(amount, 4, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }
    
    public boolean isOverBudget() {
        return spentAmount.compareTo(amount) > 0;
    }
    
    public boolean isNearLimit() {
        return getSpentPercentage().compareTo(BigDecimal.valueOf(alertThreshold)) >= 0;
    }
    
    public boolean isActiveBudget() {
        return isActive && status == BudgetStatus.ACTIVE;
    }
    
    public String getFormattedAmount() {
        return String.format("$%.2f", amount);
    }
    
    public String getFormattedSpentAmount() {
        return String.format("$%.2f", spentAmount);
    }
    
    public String getFormattedRemainingAmount() {
        return String.format("$%.2f", getRemainingAmount());
    }
    
    public String getCategoryName() {
        return category != null ? category.getName() : "All Categories";
    }
    
    public void addSpentAmount(BigDecimal amount) {
        this.spentAmount = this.spentAmount.add(amount);
    }
    
    public void resetSpentAmount() {
        this.spentAmount = BigDecimal.ZERO;
    }
    
    @Override
    public String toString() {
        return "Budget{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", amount=" + amount +
                ", spentAmount=" + spentAmount +
                ", period=" + period +
                ", status=" + status +
                '}';
    }
    
    // Budget Period Enum
    public enum BudgetPeriod {
        WEEKLY("Weekly"),
        MONTHLY("Monthly"),
        QUARTERLY("Quarterly"),
        YEARLY("Yearly");
        
        private final String displayName;
        
        BudgetPeriod(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    // Budget Status Enum
    public enum BudgetStatus {
        ACTIVE("Active"),
        INACTIVE("Inactive"),
        COMPLETED("Completed"),
        CANCELLED("Cancelled");
        
        private final String displayName;
        
        BudgetStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
} 