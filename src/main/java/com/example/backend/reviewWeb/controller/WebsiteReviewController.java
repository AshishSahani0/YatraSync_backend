package com.example.backend.reviewWeb.controller;

import com.example.backend.reviewWeb.dto.WebsiteReviewRequest;
import com.example.backend.reviewWeb.dto.WebsiteReviewResponse;
import com.example.backend.reviewWeb.service.WebsiteReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class WebsiteReviewController {

    private final WebsiteReviewService websiteReviewService;

    /**
     * Get the most recent website reviews.
     * Serves from high-performance in-memory cache directly (O(1), zero DB hits).
     */
    @GetMapping("/api/public/website-reviews/recent")
    public ResponseEntity<List<WebsiteReviewResponse>> getRecentReviews() {
        List<WebsiteReviewResponse> response = websiteReviewService.getRecentReviews();
        return ResponseEntity.ok(response);
    }

    /**
     * Submit a website rating & feedback review — requires authentication.
     * User identity is always taken from principal; anonymous reviews are not allowed.
     */
    @PostMapping("/api/user/website-reviews")
    public ResponseEntity<WebsiteReviewResponse> addReview(
            @RequestBody WebsiteReviewRequest request,
            Authentication auth
    ) {
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            return ResponseEntity.status(401).build();
        }
        String userId = auth.getName();
        WebsiteReviewResponse response = websiteReviewService.addReview(userId, request);
        return ResponseEntity.ok(response);
    }
}
