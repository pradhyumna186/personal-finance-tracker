package com.pft.controller;

import com.pft.dto.AccountDto;
import com.pft.service.AccountService;
import com.pft.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
@Tag(name = "Accounts", description = "Account management endpoints")
public class AccountController {
    
    private final AccountService accountService;
    private final UserService userService;
    
    @GetMapping
    public ResponseEntity<List<AccountDto>> getCurrentUserAccounts(Authentication authentication) {
        String userEmail = authentication.getName();
        Long userId = userService.getUserEntityByEmail(userEmail).getId();
        List<AccountDto> accounts = accountService.getAccountsByUserId(userId);
        return ResponseEntity.ok(accounts);
    }
    
    @GetMapping("/{accountId}")
    public ResponseEntity<AccountDto> getAccountById(
            @PathVariable Long accountId,
            Authentication authentication) {
        String userEmail = authentication.getName();
        Long userId = userService.getUserEntityByEmail(userEmail).getId();
        AccountDto account = accountService.getAccountById(accountId, userId);
        return ResponseEntity.ok(account);
    }
    
    @PostMapping
    public ResponseEntity<AccountDto> createAccount(
            @RequestBody AccountDto accountDto,
            Authentication authentication) {
        String userEmail = authentication.getName();
        Long userId = userService.getUserEntityByEmail(userEmail).getId();
        AccountDto createdAccount = accountService.createAccount(accountDto, userId);
        return ResponseEntity.ok(createdAccount);
    }
    
    @PutMapping("/{accountId}")
    public ResponseEntity<AccountDto> updateAccount(
            @PathVariable Long accountId,
            @RequestBody AccountDto accountDto,
            Authentication authentication) {
        String userEmail = authentication.getName();
        Long userId = userService.getUserEntityByEmail(userEmail).getId();
        AccountDto updatedAccount = accountService.updateAccount(accountId, accountDto, userId);
        return ResponseEntity.ok(updatedAccount);
    }
    
    @DeleteMapping("/{accountId}")
    public ResponseEntity<Void> deleteAccount(
            @PathVariable Long accountId,
            Authentication authentication) {
        String userEmail = authentication.getName();
        Long userId = userService.getUserEntityByEmail(userEmail).getId();
        accountService.deleteAccount(accountId, userId);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/active")
    public ResponseEntity<List<AccountDto>> getActiveAccounts(Authentication authentication) {
        String userEmail = authentication.getName();
        Long userId = userService.getUserEntityByEmail(userEmail).getId();
        List<AccountDto> activeAccounts = accountService.getActiveAccountsByUserId(userId);
        return ResponseEntity.ok(activeAccounts);
    }
    
    @GetMapping("/balance/total")
    public ResponseEntity<Object> getTotalBalance(Authentication authentication) {
        String userEmail = authentication.getName();
        Long userId = userService.getUserEntityByEmail(userEmail).getId();
        return ResponseEntity.ok(Map.of("totalBalance", accountService.getTotalBalanceByUserId(userId)));
    }
    
    @GetMapping("/default")
    public ResponseEntity<AccountDto> getDefaultAccount(Authentication authentication) {
        String userEmail = authentication.getName();
        Long userId = userService.getUserEntityByEmail(userEmail).getId();
        AccountDto defaultAccount = accountService.getDefaultAccountByUserId(userId);
        return ResponseEntity.ok(defaultAccount);
    }
    
    @PutMapping("/{accountId}/default")
    public ResponseEntity<AccountDto> setDefaultAccount(
            @PathVariable Long accountId,
            Authentication authentication) {
        String userEmail = authentication.getName();
        Long userId = userService.getUserEntityByEmail(userEmail).getId();
        AccountDto defaultAccount = accountService.setDefaultAccount(accountId, userId);
        return ResponseEntity.ok(defaultAccount);
    }
    
    @GetMapping("/stats")
    public ResponseEntity<Object> getAccountStatistics(Authentication authentication) {
        String userEmail = authentication.getName();
        Long userId = userService.getUserEntityByEmail(userEmail).getId();
        long totalAccounts = accountService.getAccountCountByUserId(userId);
        
        return ResponseEntity.ok(Map.of(
            "totalAccounts", totalAccounts
        ));
    }
} 