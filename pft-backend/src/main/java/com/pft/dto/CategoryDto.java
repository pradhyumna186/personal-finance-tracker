package com.pft.dto;

import com.pft.entity.Category;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CategoryDto {
    
    private Long id;
    private String name;
    private String description;
    private Category.CategoryType type;
    private Category.CategoryStatus status;
    private String color;
    private String icon;
    private boolean isDefault;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // User info (without sensitive data)
    private Long userId;
    private String userFullName;
    
    public static CategoryDto fromEntity(Category category) {
        CategoryDto dto = new CategoryDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        dto.setType(category.getType());
        dto.setStatus(category.getStatus());
        dto.setColor(category.getColor());
        dto.setIcon(category.getIcon());
        dto.setDefault(category.isDefault());
        dto.setCreatedAt(category.getCreatedAt());
        dto.setUpdatedAt(category.getUpdatedAt());
        
        if (category.getUser() != null) {
            dto.setUserId(category.getUser().getId());
            dto.setUserFullName(category.getUser().getFullName());
        }
        
        return dto;
    }
} 