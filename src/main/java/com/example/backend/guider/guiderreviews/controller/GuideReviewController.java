package com.example.backend.guider.guiderreviews.controller;

import com.example.backend.guider.guiderreviews.dto.GuideReviewRequest;
import com.example.backend.guider.guiderreviews.dto.GuideReviewResponse;
import com.example.backend.guider.guiderreviews.service.GuideReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class GuideReviewController {

    private final GuideReviewService guideReviewService;

    /**
     * Get reviews for a guide (Public)
     */
    @GetMapping("/api/public/guides/{guideId}/reviews")
    public ResponseEntity<Page<GuideReviewResponse>> getReviews(
            @PathVariable String guideId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<GuideReviewResponse> response = guideReviewService.getReviewsForGuide(guideId, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Add or update review — requires authentication.
     */
    @PostMapping("/api/user/guides/{guideId}/reviews")
    public ResponseEntity<GuideReviewResponse> addOrUpdateReview(
            @PathVariable String guideId,
            @RequestBody GuideReviewRequest request,
            Authentication auth
    ) {
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            return ResponseEntity.status(401).build();
        }
        String userId = auth.getName();
        GuideReviewResponse response = guideReviewService.addOrUpdateReview(guideId, userId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete review (Authenticated User)
     */
    @DeleteMapping("/api/user/guides/{guideId}/reviews")
    public ResponseEntity<Void> deleteReview(
            @PathVariable String guideId,
            Authentication auth
    ) {
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            return ResponseEntity.status(401).build();
        }
        String userId = auth.getName();
        guideReviewService.deleteReview(guideId, userId);
        return ResponseEntity.ok().build();
    }
}
