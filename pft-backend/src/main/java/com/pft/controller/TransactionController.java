package com.pft.controller;

import com.pft.dto.TransactionDto;
import com.pft.service.TransactionService;
import com.pft.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
@Tag(name = "Transactions", description = "Transaction management endpoints")
public class TransactionController {
    
    private final TransactionService transactionService;
    private final UserService userService;
    
    @GetMapping
    public ResponseEntity<List<TransactionDto>> getCurrentUserTransactions(Authentication authentication) {
        String userEmail = authentication.getName();
        Long userId = userService.getUserEntityByEmail(userEmail).getId();
        List<TransactionDto> transactions = transactionService.getTransactionsByUserId(userId);
        return ResponseEntity.ok(transactions);
    }
    
    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionDto> getTransactionById(
            @PathVariable Long transactionId,
            Authentication authentication) {
        String userEmail = authentication.getName();
        Long userId = userService.getUserEntityByEmail(userEmail).getId();
        TransactionDto transaction = transactionService.getTransactionById(transactionId, userId);
        return ResponseEntity.ok(transaction);
    }
    
    @PostMapping
    public ResponseEntity<TransactionDto> createTransaction(
            @RequestBody TransactionDto transactionDto,
            Authentication authentication) {
        String userEmail = authentication.getName();
        Long userId = userService.getUserEntityByEmail(userEmail).getId();
        TransactionDto createdTransaction = transactionService.createTransaction(transactionDto, userId);
        return ResponseEntity.ok(createdTransaction);
    }
    
    @PutMapping("/{transactionId}")
    public ResponseEntity<TransactionDto> updateTransaction(
            @PathVariable Long transactionId,
            @RequestBody TransactionDto transactionDto,
            Authentication authentication) {
        String userEmail = authentication.getName();
        Long userId = userService.getUserEntityByEmail(userEmail).getId();
        TransactionDto updatedTransaction = transactionService.updateTransaction(transactionId, transactionDto, userId);
        return ResponseEntity.ok(updatedTransaction);
    }
    
    @DeleteMapping("/{transactionId}")
    public ResponseEntity<Void> deleteTransaction(
            @PathVariable Long transactionId,
            Authentication authentication) {
        String userEmail = authentication.getName();
        Long userId = userService.getUserEntityByEmail(userEmail).getId();
        transactionService.deleteTransaction(transactionId, userId);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<TransactionDto>> getTransactionsByAccount(
            @PathVariable Long accountId,
            Authentication authentication) {
        String userEmail = authentication.getName();
        Long userId = userService.getUserEntityByEmail(userEmail).getId();
        List<TransactionDto> transactions = transactionService.getTransactionsByAccountId(accountId, userId);
        return ResponseEntity.ok(transactions);
    }
    
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<TransactionDto>> getTransactionsByCategory(
            @PathVariable Long categoryId,
            Authentication authentication) {
        String userEmail = authentication.getName();
        Long userId = userService.getUserEntityByEmail(userEmail).getId();
        List<TransactionDto> transactions = transactionService.getTransactionsByUserIdAndCategoryId(userId, categoryId);
        return ResponseEntity.ok(transactions);
    }
    
    @GetMapping("/date-range")
    public ResponseEntity<List<TransactionDto>> getTransactionsByDateRange(
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate,
            Authentication authentication) {
        String userEmail = authentication.getName();
        Long userId = userService.getUserEntityByEmail(userEmail).getId();
        List<TransactionDto> transactions = transactionService.getTransactionsByUserIdAndDateRange(userId, startDate, endDate);
        return ResponseEntity.ok(transactions);
    }
    
    @GetMapping("/recurring")
    public ResponseEntity<List<TransactionDto>> getRecurringTransactions() {
        List<TransactionDto> recurringTransactions = transactionService.getRecurringTransactions();
        return ResponseEntity.ok(recurringTransactions);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<TransactionDto>> searchTransactions(
            @RequestParam String description,
            Authentication authentication) {
        String userEmail = authentication.getName();
        Long userId = userService.getUserEntityByEmail(userEmail).getId();
        List<TransactionDto> transactions = transactionService.searchTransactionsByDescription(description);
        return ResponseEntity.ok(transactions);
    }
    
    @GetMapping("/stats")
    public ResponseEntity<Object> getTransactionStatistics(Authentication authentication) {
        String userEmail = authentication.getName();
        Long userId = userService.getUserEntityByEmail(userEmail).getId();
        long totalTransactions = transactionService.getTransactionCountByUserId(userId);
        
        return ResponseEntity.ok(Map.of(
            "totalTransactions", totalTransactions
        ));
    }
} 