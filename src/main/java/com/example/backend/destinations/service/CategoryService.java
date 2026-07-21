package com.example.backend.destinations.service;
import com.example.backend.destinations.model.Category;
import com.example.backend.destinations.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository repository;


    public Category createCategory(Category req, Authentication auth) {


        if (req.getName() == null || req.getName().isBlank()) {
            throw new RuntimeException("Category name required");
        }

        String name = req.getName().trim();


        String slug = generateUniqueSlug(name);

        Category category = Category.builder()
                .name(name)
                .slug(slug)


                .icon(req.getIcon())
                .imageUrl(req.getImageUrl())
                .description(req.getDescription())


                .isActive(Boolean.TRUE.equals(req.getIsActive()))
                .priority(req.getPriority() != null ? req.getPriority() : 0)

                .metaTitle(req.getMetaTitle() != null ? req.getMetaTitle() : name)
                .metaDescription(req.getMetaDescription() != null ? req.getMetaDescription() : req.getDescription())


                .createdBy(auth.getName())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())

                .build();

        return repository.save(category);
    }

    private String generateUniqueSlug(String name) {
        String base = name.toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");

        String slug = base;
        int count = 1;

        while (repository.findBySlug(slug).isPresent()) {
            slug = base + "-" + count++;
        }

        return slug;
    }

    public List<Category> getActiveCategories() {

        return repository.findByIsActiveTrueOrderByPriorityAscNameAsc();
    }

    public void deleteCategory(String id) {

        Category category = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));


        category.setIsActive(false);
        category.setUpdatedAt(LocalDateTime.now());

        repository.save(category);
    }


}