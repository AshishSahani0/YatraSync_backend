package com.example.backend.destinations.repository;


import com.example.backend.destinations.model.Category;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends MongoRepository<Category, String> {

    Optional<Category> findBySlug(String slug);

    boolean existsBySlug(String slug);

    List<Category> findBySlugStartingWith(String prefix);



    List<Category> findByIsActiveTrueOrderByPriorityAscNameAsc();
}