package com.pft.controller;

import com.pft.dto.BudgetDto;
import com.pft.service.BudgetService;
import com.pft.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/budgets")
@RequiredArgsConstructor
@Tag(name = "Budgets", description = "Budget management endpoints")
public class BudgetController {
    
    private final BudgetService budgetService;
    private final UserService userService;
    
    @GetMapping
    public ResponseEntity<List<BudgetDto>> getCurrentUserBudgets(Authentication authentication) {
        String userEmail = authentication.getName();
        Long userId = userService.getUserEntityByEmail(userEmail).getId();
        List<BudgetDto> budgets = budgetService.getBudgetsByUserId(userId);
        return ResponseEntity.ok(budgets);
    }
    
    @GetMapping("/{budgetId}")
    public ResponseEntity<BudgetDto> getBudgetById(
            @PathVariable Long budgetId,
            Authentication authentication) {
        String userEmail = authentication.getName();
        Long userId = userService.getUserEntityByEmail(userEmail).getId();
        BudgetDto budget = budgetService.getBudgetById(budgetId, userId);
        return ResponseEntity.ok(budget);
    }
    
    @PostMapping
    public ResponseEntity<BudgetDto> createBudget(
            @RequestBody BudgetDto budgetDto,
            Authentication authentication) {
        String userEmail = authentication.getName();
        Long userId = userService.getUserEntityByEmail(userEmail).getId();
        BudgetDto createdBudget = budgetService.createBudget(budgetDto, userId);
        return ResponseEntity.ok(createdBudget);
    }
    
    @PutMapping("/{budgetId}")
    public ResponseEntity<BudgetDto> updateBudget(
            @PathVariable Long budgetId,
            @RequestBody BudgetDto budgetDto,
            Authentication authentication) {
        String userEmail = authentication.getName();
        Long userId = userService.getUserEntityByEmail(userEmail).getId();
        BudgetDto updatedBudget = budgetService.updateBudget(budgetId, budgetDto, userId);
        return ResponseEntity.ok(updatedBudget);
    }
    
    @DeleteMapping("/{budgetId}")
    public ResponseEntity<Void> deleteBudget(
            @PathVariable Long budgetId,
            Authentication authentication) {
        String userEmail = authentication.getName();
        Long userId = userService.getUserEntityByEmail(userEmail).getId();
        budgetService.deleteBudget(budgetId, userId);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/active")
    public ResponseEntity<List<BudgetDto>> getActiveBudgets(Authentication authentication) {
        String userEmail = authentication.getName();
        Long userId = userService.getUserEntityByEmail(userEmail).getId();
        List<BudgetDto> activeBudgets = budgetService.getActiveBudgetsByUserId(userId);
        return ResponseEntity.ok(activeBudgets);
    }
    
    @GetMapping("/over-budget")
    public ResponseEntity<List<BudgetDto>> getOverBudgetBudgets(Authentication authentication) {
        String userEmail = authentication.getName();
        Long userId = userService.getUserEntityByEmail(userEmail).getId();
        List<BudgetDto> overBudgetBudgets = budgetService.getOverBudgetBudgetsByUserId(userId);
        return ResponseEntity.ok(overBudgetBudgets);
    }
    
    @GetMapping("/near-limit")
    public ResponseEntity<List<BudgetDto>> getBudgetsNearLimit(Authentication authentication) {
        String userEmail = authentication.getName();
        Long userId = userService.getUserEntityByEmail(userEmail).getId();
        List<BudgetDto> budgetsNearLimit = budgetService.getBudgetsNearLimitByUserId(userId);
        return ResponseEntity.ok(budgetsNearLimit);
    }
    
    @PostMapping("/{budgetId}/add-spent")
    public ResponseEntity<Void> addSpentAmount(
            @PathVariable Long budgetId,
            @RequestParam BigDecimal amount,
            Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        budgetService.addSpentAmount(budgetId, amount, userId);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/{budgetId}/reset-spent")
    public ResponseEntity<Void> resetSpentAmount(
            @PathVariable Long budgetId,
            Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        budgetService.resetSpentAmount(budgetId, userId);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<BudgetDto>> searchBudgets(
            @RequestParam String name,
            Authentication authentication) {
        String userEmail = authentication.getName();
        Long userId = userService.getUserEntityByEmail(userEmail).getId();
        List<BudgetDto> budgets = budgetService.searchBudgetsByName(name);
        return ResponseEntity.ok(budgets);
    }
    
    @GetMapping("/stats")
    public ResponseEntity<Object> getBudgetStatistics(Authentication authentication) {
        String userEmail = authentication.getName();
        Long userId = userService.getUserEntityByEmail(userEmail).getId();
        long totalBudgets = budgetService.getBudgetCountByUserId(userId);
        long activeBudgets = budgetService.getActiveBudgetCountByUserId(userId);
        BigDecimal totalBudgetAmount = budgetService.getTotalBudgetAmountByUserId(userId);
        BigDecimal totalSpentAmount = budgetService.getTotalSpentAmountByUserId(userId);
        
        return ResponseEntity.ok(Map.of(
            "totalBudgets", totalBudgets,
            "activeBudgets", activeBudgets,
            "totalBudgetAmount", totalBudgetAmount,
            "totalSpentAmount", totalSpentAmount
        ));
    }
} 