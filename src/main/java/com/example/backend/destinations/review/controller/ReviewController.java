package com.example.backend.destinations.review.controller;

import com.example.backend.destinations.review.dto.ReviewRequest;
import com.example.backend.destinations.review.dto.ReviewResponse;
import com.example.backend.destinations.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * Get reviews for a destination (Public)
     */
    @GetMapping("/api/public/destinations/{destinationId}/reviews")
    public ResponseEntity<Page<ReviewResponse>> getReviews(
            @PathVariable String destinationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ReviewResponse> response = reviewService.getReviewsForDestination(destinationId, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Add or update review — requires authentication.
     * User identity is always taken from the JWT principal; anonymous reviews are not allowed.
     */
    @PostMapping("/api/user/destinations/{destinationId}/reviews")
    public ResponseEntity<ReviewResponse> addOrUpdateReview(
            @PathVariable String destinationId,
            @RequestBody ReviewRequest request,
            Authentication auth
    ) {
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            return ResponseEntity.status(401).build();
        }
        String userId = auth.getName();
        ReviewResponse response = reviewService.addOrUpdateReview(destinationId, userId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete review (Authenticated User)
     */
    @DeleteMapping("/api/user/destinations/{destinationId}/reviews")
    public ResponseEntity<Void> deleteReview(
            @PathVariable String destinationId,
            Authentication auth
    ) {
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            return ResponseEntity.status(401).build();
        }
        String userId = auth.getName();
        reviewService.deleteReview(destinationId, userId);
        return ResponseEntity.ok().build();
    }
}
