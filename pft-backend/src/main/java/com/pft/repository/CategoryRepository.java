package com.pft.repository;

import com.pft.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    /**
     * Find categories by user ID
     */
    List<Category> findByUserId(Long userId);
    
    /**
     * Find categories by user ID and type
     */
    List<Category> findByUserIdAndType(Long userId, Category.CategoryType type);
    
    /**
     * Find categories by user ID and status
     */
    List<Category> findByUserIdAndStatus(Long userId, Category.CategoryStatus status);
    
    /**
     * Find category by name and user ID
     */
    Optional<Category> findByNameAndUserId(String name, Long userId);
    
    /**
     * Find default categories
     */
    List<Category> findByIsDefaultTrue();
    
    /**
     * Find categories by name containing (case insensitive)
     */
    List<Category> findByNameContainingIgnoreCase(String name);
    
    /**
     * Find active categories by user ID
     */
    @Query("SELECT c FROM Category c WHERE c.user.id = :userId AND c.status = 'ACTIVE'")
    List<Category> findActiveCategoriesByUserId(@Param("userId") Long userId);
    
    /**
     * Find categories by type for a specific user
     */
    @Query("SELECT c FROM Category c WHERE c.user.id = :userId AND c.type = :type AND c.status = 'ACTIVE'")
    List<Category> findActiveCategoriesByUserIdAndType(@Param("userId") Long userId, @Param("type") Category.CategoryType type);
    
    /**
     * Count categories by user ID
     */
    long countByUserId(Long userId);
    
    /**
     * Count categories by user ID and type
     */
    long countByUserIdAndType(Long userId, Category.CategoryType type);
    
    /**
     * Check if category exists by name and user ID
     */
    boolean existsByNameAndUserId(String name, Long userId);
} 