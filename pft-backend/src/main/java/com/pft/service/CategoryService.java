package com.pft.service;

import com.pft.dto.CategoryDto;
import com.pft.entity.Category;
import com.pft.entity.User;
import com.pft.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {
    
    private final CategoryRepository categoryRepository;
    private final UserService userService;
    
    public CategoryDto getCategoryById(Long categoryId, Long userId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found with ID: " + categoryId));
        
        // Verify ownership
        if (!category.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied: Category does not belong to user");
        }
        
        return CategoryDto.fromEntity(category);
    }
    
    public List<CategoryDto> getCategoriesByUserId(Long userId) {
        return categoryRepository.findByUserId(userId).stream()
                .map(CategoryDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    public List<CategoryDto> getActiveCategoriesByUserId(Long userId) {
        return categoryRepository.findActiveCategoriesByUserId(userId).stream()
                .map(CategoryDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    public List<CategoryDto> getCategoriesByUserIdAndType(Long userId, Category.CategoryType type) {
        return categoryRepository.findActiveCategoriesByUserIdAndType(userId, type).stream()
                .map(CategoryDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public CategoryDto createCategory(CategoryDto categoryDto, Long userId) {
        User user = userService.getUserEntityById(userId);
        
        // Check if category name already exists for this user
        if (categoryRepository.existsByNameAndUserId(categoryDto.getName(), userId)) {
            throw new RuntimeException("Category with name '" + categoryDto.getName() + "' already exists");
        }
        
        Category category = new Category();
        category.setName(categoryDto.getName());
        category.setDescription(categoryDto.getDescription());
        category.setType(categoryDto.getType());
        category.setColor(categoryDto.getColor());
        category.setIcon(categoryDto.getIcon());
        category.setUser(user);
        
        Category savedCategory = categoryRepository.save(category);
        return CategoryDto.fromEntity(savedCategory);
    }
    
    @Transactional
    public CategoryDto updateCategory(Long categoryId, CategoryDto categoryDto, Long userId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found with ID: " + categoryId));
        
        // Verify ownership
        if (!category.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied: Category does not belong to user");
        }
        
        // Check if new name conflicts with existing category (excluding current category)
        if (categoryDto.getName() != null && !categoryDto.getName().equals(category.getName())) {
            if (categoryRepository.existsByNameAndUserId(categoryDto.getName(), userId)) {
                throw new RuntimeException("Category with name '" + categoryDto.getName() + "' already exists");
            }
        }
        
        // Update fields
        if (categoryDto.getName() != null) {
            category.setName(categoryDto.getName());
        }
        if (categoryDto.getDescription() != null) {
            category.setDescription(categoryDto.getDescription());
        }
        if (categoryDto.getColor() != null) {
            category.setColor(categoryDto.getColor());
        }
        if (categoryDto.getIcon() != null) {
            category.setIcon(categoryDto.getIcon());
        }
        if (categoryDto.getStatus() != null) {
            category.setStatus(categoryDto.getStatus());
        }
        
        Category updatedCategory = categoryRepository.save(category);
        return CategoryDto.fromEntity(updatedCategory);
    }
    
    @Transactional
    public void deleteCategory(Long categoryId, Long userId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found with ID: " + categoryId));
        
        // Verify ownership
        if (!category.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied: Category does not belong to user");
        }
        
        // Check if category is default (prevent deletion of default categories)
        if (category.isDefault()) {
            throw new RuntimeException("Cannot delete default category");
        }
        
        // Check if category has transactions
        if (!category.getTransactions().isEmpty()) {
            throw new RuntimeException("Cannot delete category with existing transactions");
        }
        
        categoryRepository.delete(category);
    }
    
    public List<CategoryDto> getDefaultCategories() {
        return categoryRepository.findByIsDefaultTrue().stream()
                .map(CategoryDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    public List<CategoryDto> searchCategoriesByName(String name) {
        return categoryRepository.findByNameContainingIgnoreCase(name).stream()
                .map(CategoryDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    public long getCategoryCountByUserId(Long userId) {
        return categoryRepository.countByUserId(userId);
    }
    
    public long getCategoryCountByUserIdAndType(Long userId, Category.CategoryType type) {
        return categoryRepository.countByUserIdAndType(userId, type);
    }
    
    // Internal method to get category entity
    public Category getCategoryEntityById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found with ID: " + categoryId));
    }
} 