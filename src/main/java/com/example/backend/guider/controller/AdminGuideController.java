package com.example.backend.guider.controller;

import com.example.backend.guider.dto.GuideRequest;
import com.example.backend.guider.dto.GuideResponse;
import com.example.backend.guider.service.GuideService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/guides")
@RequiredArgsConstructor
public class AdminGuideController {

    private final GuideService guideService;

    @PostMapping
    public ResponseEntity<GuideResponse> create(
            @RequestBody GuideRequest request,
            Authentication auth
    ) {
        String adminId = auth.getName();
        return ResponseEntity.ok(guideService.createGuide(request, adminId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GuideResponse> update(
            @PathVariable String id,
            @RequestBody GuideRequest request,
            Authentication auth
    ) {
        String adminId = auth.getName();
        return ResponseEntity.ok(guideService.updateGuide(id, request, adminId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable String id
    ) {
        guideService.deleteGuide(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<GuideResponse>> getAll(
            Pageable pageable
    ) {
        return ResponseEntity.ok(guideService.getAllForAdmin(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GuideResponse> getById(
            @PathVariable String id
    ) {
        return ResponseEntity.ok(guideService.getByIdForAdmin(id));
    }
}
