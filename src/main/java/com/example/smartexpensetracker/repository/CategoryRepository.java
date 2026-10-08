package com.example.smartexpensetracker.repository;

import com.example.smartexpensetracker.entity.Category;
import com.example.smartexpensetracker.entity.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for Category entity.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByTypeOrderByNameAsc(TransactionType type);

    Optional<Category> findByNameAndType(String name, TransactionType type);

    boolean existsByNameAndType(String name, TransactionType type);
}
