package com.pft.controller;

import com.pft.dto.UserDto;
import com.pft.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    @GetMapping("/profile")
    public ResponseEntity<UserDto> getCurrentUserProfile(Authentication authentication) {
        String userEmail = authentication.getName();
        UserDto userDto = userService.getUserByEmail(userEmail);
        return ResponseEntity.ok(userDto);
    }
    
    @PutMapping("/profile")
    public ResponseEntity<UserDto> updateCurrentUserProfile(
            @RequestBody UserDto userDto,
            Authentication authentication) {
        String userEmail = authentication.getName();
        UserDto updatedUser = userService.updateUserProfileByEmail(userEmail, userDto);
        return ResponseEntity.ok(updatedUser);
    }
    
    @DeleteMapping("/profile")
    public ResponseEntity<Void> deleteCurrentUserProfile(Authentication authentication) {
        String userEmail = authentication.getName();
        userService.deleteUserByEmail(userEmail);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<UserDto>> searchUsersByName(@RequestParam String name) {
        List<UserDto> users = userService.searchUsersByName(name);
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getUserStatistics() {
        long totalUsers = userService.getTotalUserCount();
        long activeUsers = userService.getActiveUserCount();
        
        return ResponseEntity.ok(Map.of(
            "totalUsers", totalUsers,
            "activeUsers", activeUsers
        ));
    }
} 