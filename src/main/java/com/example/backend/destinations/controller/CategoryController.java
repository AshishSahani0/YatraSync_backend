package com.example.backend.destinations.controller;


import com.example.backend.destinations.model.Category;
import com.example.backend.destinations.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CategoryController {

    private final CategoryService service;


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/categories")
    public ResponseEntity<Category> create(
            @RequestBody Category request,
            Authentication auth
    ) {
        Category category = service.createCategory(request, auth);
        return ResponseEntity.status(201).body(category);
    }

    @GetMapping("/public/categories")
    public List<Category> getAll() {
        return service.getActiveCategories();
    }


    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/categories/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        service.deleteCategory(id);

        return ResponseEntity.ok("Category deactivated");
    }
}