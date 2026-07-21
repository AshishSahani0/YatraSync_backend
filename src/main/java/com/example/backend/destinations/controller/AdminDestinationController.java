package com.example.backend.destinations.controller;

import com.example.backend.destinations.dto.DestinationRequest;
import com.example.backend.destinations.dto.DestinationResponse;
import com.example.backend.destinations.service.DestinationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/admin/destinations")
@RequiredArgsConstructor
public class AdminDestinationController {

    private final DestinationService service;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<DestinationResponse> createDestination(
            @Valid @RequestBody DestinationRequest request,
            Authentication auth
    ) {
        String adminId = auth.getName();
        DestinationResponse response = service.createDestination(request, adminId);
        return ResponseEntity.status(201).body(response);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public Page<DestinationResponse> getAll(Pageable pageable) {
        return service.getAllForAdmin(pageable);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public DestinationResponse getById(@PathVariable String id) {
        return service.getByIdForAdmin(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DestinationResponse> update(
            @PathVariable String id,
            @RequestBody DestinationRequest request,
            Authentication auth
    ) {
        String adminId = auth.getName();

        return ResponseEntity.ok(
                service.updateDestination(id, request, adminId)
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> delete(@PathVariable String id) {
        service.deleteDestination(id);
        return ResponseEntity.ok("Deleted");
    }
}
