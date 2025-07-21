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
@Table(name = "goals")
@EntityListeners(AuditingEntityListener.class)
public class Goal {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Goal name is required")
    @Column(name = "name", nullable = false)
    private String name;
    
    @NotNull(message = "Target amount is required")
    @Column(name = "target_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal targetAmount;
    
    @Column(name = "current_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal currentAmount = BigDecimal.ZERO;
    
    @Column(name = "description")
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private GoalType type;
    
    @Column(name = "target_date")
    private LocalDateTime targetDate;
    
    @Column(name = "color", length = 7)
    private String color = "#8B5CF6"; // Default purple color
    
    @Column(name = "icon")
    private String icon = "flag"; // Default icon
    
    @Column(name = "is_primary")
    private boolean isPrimary = false;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GoalStatus status = GoalStatus.ACTIVE;
    
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
    
    // Constructors
    public Goal() {}
    
    public Goal(String name, BigDecimal targetAmount, GoalType type, User user) {
        this.name = name;
        this.targetAmount = targetAmount;
        this.type = type;
        this.user = user;
    }
    
    public Goal(String name, BigDecimal targetAmount, GoalType type, 
                LocalDateTime targetDate, User user) {
        this.name = name;
        this.targetAmount = targetAmount;
        this.type = type;
        this.targetDate = targetDate;
        this.user = user;
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
    
    public BigDecimal getTargetAmount() {
        return targetAmount;
    }
    
    public void setTargetAmount(BigDecimal targetAmount) {
        this.targetAmount = targetAmount;
    }
    
    public BigDecimal getCurrentAmount() {
        return currentAmount;
    }
    
    public void setCurrentAmount(BigDecimal currentAmount) {
        this.currentAmount = currentAmount;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public GoalType getType() {
        return type;
    }
    
    public void setType(GoalType type) {
        this.type = type;
    }
    
    public LocalDateTime getTargetDate() {
        return targetDate;
    }
    
    public void setTargetDate(LocalDateTime targetDate) {
        this.targetDate = targetDate;
    }
    
    public String getColor() {
        return color;
    }
    
    public void setColor(String color) {
        this.color = color;
    }
    
    public String getIcon() {
        return icon;
    }
    
    public void setIcon(String icon) {
        this.icon = icon;
    }
    
    public boolean isPrimary() {
        return isPrimary;
    }
    
    public void setPrimary(boolean primary) {
        isPrimary = primary;
    }
    
    public GoalStatus getStatus() {
        return status;
    }
    
    public void setStatus(GoalStatus status) {
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
    
    // Helper methods
    public BigDecimal getRemainingAmount() {
        return targetAmount.subtract(currentAmount);
    }
    
    public BigDecimal getProgressPercentage() {
        if (targetAmount.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return currentAmount.divide(targetAmount, 4, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }
    
    public boolean isCompleted() {
        return currentAmount.compareTo(targetAmount) >= 0;
    }
    
    public boolean isOverdue() {
        return targetDate != null && LocalDateTime.now().isAfter(targetDate) && !isCompleted();
    }
    
    public boolean isActive() {
        return status == GoalStatus.ACTIVE;
    }
    
    public String getFormattedTargetAmount() {
        return String.format("$%.2f", targetAmount);
    }
    
    public String getFormattedCurrentAmount() {
        return String.format("$%.2f", currentAmount);
    }
    
    public String getFormattedRemainingAmount() {
        return String.format("$%.2f", getRemainingAmount());
    }
    
    public void addAmount(BigDecimal amount) {
        this.currentAmount = this.currentAmount.add(amount);
        if (isCompleted()) {
            this.status = GoalStatus.COMPLETED;
        }
    }
    
    public void resetProgress() {
        this.currentAmount = BigDecimal.ZERO;
        this.status = GoalStatus.ACTIVE;
    }
    
    public long getDaysRemaining() {
        if (targetDate == null) {
            return -1; // No target date set
        }
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(targetDate)) {
            return 0; // Overdue
        }
        return java.time.Duration.between(now, targetDate).toDays();
    }
    
    @Override
    public String toString() {
        return "Goal{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", targetAmount=" + targetAmount +
                ", currentAmount=" + currentAmount +
                ", type=" + type +
                ", status=" + status +
                '}';
    }
    
    // Goal Type Enum
    public enum GoalType {
        SAVINGS("Savings"),
        DEBT_PAYOFF("Debt Payoff"),
        EMERGENCY_FUND("Emergency Fund"),
        INVESTMENT("Investment"),
        PURCHASE("Purchase"),
        TRAVEL("Travel"),
        EDUCATION("Education"),
        OTHER("Other");
        
        private final String displayName;
        
        GoalType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    // Goal Status Enum
    public enum GoalStatus {
        ACTIVE("Active"),
        COMPLETED("Completed"),
        PAUSED("Paused"),
        CANCELLED("Cancelled");
        
        private final String displayName;
        
        GoalStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
} 