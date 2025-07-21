package com.pft.controller;

import com.pft.dto.GoalDto;
import com.pft.service.GoalService;
import com.pft.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/goals")
@RequiredArgsConstructor
@Tag(name = "Goals", description = "Goal management endpoints")
public class GoalController {
    
    private final GoalService goalService;
    private final UserService userService;
    
    @GetMapping
    public ResponseEntity<List<GoalDto>> getCurrentUserGoals(Authentication authentication) {
        String userEmail = authentication.getName();
        Long userId = userService.getUserEntityByEmail(userEmail).getId();
        List<GoalDto> goals = goalService.getGoalsByUserId(userId);
        return ResponseEntity.ok(goals);
    }
    
    @GetMapping("/{goalId}")
    public ResponseEntity<GoalDto> getGoalById(
            @PathVariable Long goalId,
            Authentication authentication) {
        String userEmail = authentication.getName();
        Long userId = userService.getUserEntityByEmail(userEmail).getId();
        GoalDto goal = goalService.getGoalById(goalId, userId);
        return ResponseEntity.ok(goal);
    }
    
    @PostMapping
    public ResponseEntity<GoalDto> createGoal(
            @RequestBody GoalDto goalDto,
            Authentication authentication) {
        String userEmail = authentication.getName();
        Long userId = userService.getUserEntityByEmail(userEmail).getId();
        GoalDto createdGoal = goalService.createGoal(goalDto, userId);
        return ResponseEntity.ok(createdGoal);
    }
    
    @PutMapping("/{goalId}")
    public ResponseEntity<GoalDto> updateGoal(
            @PathVariable Long goalId,
            @RequestBody GoalDto goalDto,
            Authentication authentication) {
        String userEmail = authentication.getName();
        Long userId = userService.getUserEntityByEmail(userEmail).getId();
        GoalDto updatedGoal = goalService.updateGoal(goalId, goalDto, userId);
        return ResponseEntity.ok(updatedGoal);
    }
    
    @DeleteMapping("/{goalId}")
    public ResponseEntity<Void> deleteGoal(
            @PathVariable Long goalId,
            Authentication authentication) {
        String userEmail = authentication.getName();
        Long userId = userService.getUserEntityByEmail(userEmail).getId();
        goalService.deleteGoal(goalId, userId);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/active")
    public ResponseEntity<List<GoalDto>> getActiveGoals(Authentication authentication) {
        String userEmail = authentication.getName();
        Long userId = userService.getUserEntityByEmail(userEmail).getId();
        List<GoalDto> activeGoals = goalService.getActiveGoalsByUserId(userId);
        return ResponseEntity.ok(activeGoals);
    }
    
    @GetMapping("/completed")
    public ResponseEntity<List<GoalDto>> getCompletedGoals(Authentication authentication) {
        String userEmail = authentication.getName();
        Long userId = userService.getUserEntityByEmail(userEmail).getId();
        List<GoalDto> completedGoals = goalService.getCompletedGoalsByUserId(userId);
        return ResponseEntity.ok(completedGoals);
    }
    
    @GetMapping("/near-completion")
    public ResponseEntity<List<GoalDto>> getGoalsNearCompletion(Authentication authentication) {
        String userEmail = authentication.getName();
        Long userId = userService.getUserEntityByEmail(userEmail).getId();
        List<GoalDto> goalsNearCompletion = goalService.getGoalsNearCompletionByUserId(userId);
        return ResponseEntity.ok(goalsNearCompletion);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<GoalDto>> searchGoals(
            @RequestParam String name,
            Authentication authentication) {
        String userEmail = authentication.getName();
        Long userId = userService.getUserEntityByEmail(userEmail).getId();
        List<GoalDto> goals = goalService.searchGoalsByName(name);
        return ResponseEntity.ok(goals);
    }
    
    @GetMapping("/stats")
    public ResponseEntity<Object> getGoalStatistics(Authentication authentication) {
        String userEmail = authentication.getName();
        Long userId = userService.getUserEntityByEmail(userEmail).getId();
        long totalGoals = goalService.getGoalCountByUserId(userId);
        
        return ResponseEntity.ok(Map.of(
            "totalGoals", totalGoals
        ));
    }
} 