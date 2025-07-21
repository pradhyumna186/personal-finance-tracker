package com.pft.dto;

import com.pft.entity.Account;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AccountDto {
    
    private Long id;
    private String name;
    private String accountNumber;
    private String institutionName;
    private Account.AccountType type;
    private Account.AccountStatus status;
    private BigDecimal currentBalance;
    private BigDecimal initialBalance;
    private String color;
    private String icon;
    private boolean isDefault;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // User info (without sensitive data)
    private Long userId;
    private String userFullName;
    
    public static AccountDto fromEntity(Account account) {
        AccountDto dto = new AccountDto();
        dto.setId(account.getId());
        dto.setName(account.getName());
        dto.setAccountNumber(account.getAccountNumber());
        dto.setInstitutionName(account.getInstitutionName());
        dto.setType(account.getType());
        dto.setStatus(account.getStatus());
        dto.setCurrentBalance(account.getCurrentBalance());
        dto.setInitialBalance(account.getInitialBalance());
        dto.setColor(account.getColor());
        dto.setIcon(account.getIcon());
        dto.setDefault(account.isDefault());
        dto.setCreatedAt(account.getCreatedAt());
        dto.setUpdatedAt(account.getUpdatedAt());
        
        if (account.getUser() != null) {
            dto.setUserId(account.getUser().getId());
            dto.setUserFullName(account.getUser().getFullName());
        }
        
        return dto;
    }
} 