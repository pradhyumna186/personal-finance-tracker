package com.pft.controller;

import com.pft.dto.CategoryDto;
import com.pft.service.CategoryService;
import com.pft.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "Category management endpoints")
public class CategoryController {
    
    private final CategoryService categoryService;
    private final UserService userService;
    
    @GetMapping
    public ResponseEntity<List<CategoryDto>> getCurrentUserCategories(Authentication authentication) {
        String userEmail = authentication.getName();
        Long userId = userService.getUserEntityByEmail(userEmail).getId();
        List<CategoryDto> categories = categoryService.getCategoriesByUserId(userId);
        return ResponseEntity.ok(categories);
    }
    
    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryDto> getCategoryById(
            @PathVariable Long categoryId,
            Authentication authentication) {
        String userEmail = authentication.getName();
        Long userId = userService.getUserEntityByEmail(userEmail).getId();
        CategoryDto category = categoryService.getCategoryById(categoryId, userId);
        return ResponseEntity.ok(category);
    }
    
    @PostMapping
    public ResponseEntity<CategoryDto> createCategory(
            @RequestBody CategoryDto categoryDto,
            Authentication authentication) {
        String userEmail = authentication.getName();
        Long userId = userService.getUserEntityByEmail(userEmail).getId();
        CategoryDto createdCategory = categoryService.createCategory(categoryDto, userId);
        return ResponseEntity.ok(createdCategory);
    }
    
    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryDto> updateCategory(
            @PathVariable Long categoryId,
            @RequestBody CategoryDto categoryDto,
            Authentication authentication) {
        String userEmail = authentication.getName();
        Long userId = userService.getUserEntityByEmail(userEmail).getId();
        CategoryDto updatedCategory = categoryService.updateCategory(categoryId, categoryDto, userId);
        return ResponseEntity.ok(updatedCategory);
    }
    
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(
            @PathVariable Long categoryId,
            Authentication authentication) {
        String userEmail = authentication.getName();
        Long userId = userService.getUserEntityByEmail(userEmail).getId();
        categoryService.deleteCategory(categoryId, userId);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/default")
    public ResponseEntity<List<CategoryDto>> getDefaultCategories() {
        List<CategoryDto> defaultCategories = categoryService.getDefaultCategories();
        return ResponseEntity.ok(defaultCategories);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<CategoryDto>> searchCategories(
            @RequestParam String name,
            Authentication authentication) {
        String userEmail = authentication.getName();
        Long userId = userService.getUserEntityByEmail(userEmail).getId();
        List<CategoryDto> categories = categoryService.searchCategoriesByName(name);
        return ResponseEntity.ok(categories);
    }
    
    @GetMapping("/stats")
    public ResponseEntity<Object> getCategoryStatistics(Authentication authentication) {
        String userEmail = authentication.getName();
        Long userId = userService.getUserEntityByEmail(userEmail).getId();
        long totalCategories = categoryService.getCategoryCountByUserId(userId);
        
        return ResponseEntity.ok(Map.of(
            "totalCategories", totalCategories
        ));
    }
} 