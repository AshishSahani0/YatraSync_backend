package com.example.backend.reviewWeb.service;

import com.example.backend.reviewWeb.dto.WebsiteReviewRequest;
import com.example.backend.reviewWeb.dto.WebsiteReviewResponse;
import com.example.backend.reviewWeb.model.WebsiteReview;
import com.example.backend.reviewWeb.repository.WebsiteReviewRepository;
import com.example.backend.user.model.User;
import com.example.backend.user.service.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebsiteReviewService {

    private final WebsiteReviewRepository websiteReviewRepository;
    private final UserService userService;

    // High-performance thread-safe in-memory cache for recent website reviews
    private final List<WebsiteReviewResponse> recentReviewsCache = new CopyOnWriteArrayList<>();

    /**
     * Preload cache with top 10 most recent reviews on application startup.
     * Guarantees that subsequent read hits are resolved instantly with O(1) memory lookup.
     */
    @PostConstruct
    public void preloadCache() {
        try {
            refreshCacheFromDb();
            log.info("Successfully preloaded website reviews cache with {} entries.", recentReviewsCache.size());
        } catch (Exception e) {
            log.error("Failed to preload website reviews cache during startup:", e);
        }
    }

    /**
     * Retrieve the most recent website reviews.
     * Serves from memory directly: extremely fast, secure, and zero database queries.
     */
    public List<WebsiteReviewResponse> getRecentReviews() {
        return recentReviewsCache;
    }

    /**
     * Submit a new website review.
     * Optionally binds authenticated traveller details, or falls back to Guest anonymity.
     */
    public WebsiteReviewResponse addReview(String userId, WebsiteReviewRequest req) {
        if (userId == null) {
            throw new IllegalArgumentException("User must be authenticated to submit website feedback.");
        }
        if (req.getRating() == null || req.getRating() < 1 || req.getRating() > 5) {
            throw new IllegalArgumentException("Star rating must be an integer between 1 and 5.");
        }

        // Logged-in traveller: Map display name & avatar
        User user = userService.getById(userId);
        String displayName = user.getName();
        if (displayName == null || displayName.trim().isEmpty()) {
            displayName = user.getUserName();
        }

        WebsiteReview review = WebsiteReview.builder()
                .userId(userId)
                .userName(displayName)
                .userAvatar(user.getProfileImage())
                .rating(req.getRating())
                .comment(req.getComment())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        WebsiteReview saved = websiteReviewRepository.save(review);
        WebsiteReviewResponse response = mapToResponse(saved);

        // Refresh cache seamlessly
        try {
            refreshCacheFromDb();
        } catch (Exception e) {
            log.error("Failed to refresh website reviews cache after write:", e);
        }

        return response;
    }

    /**
     * Thread-safe cache synchronizer mapping fresh DB states.
     */
    private void refreshCacheFromDb() {
        List<WebsiteReview> dbReviews = websiteReviewRepository.findTop10ByOrderByCreatedAtDesc();
        List<WebsiteReviewResponse> responses = dbReviews.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        recentReviewsCache.clear();
        recentReviewsCache.addAll(responses);
    }

    private WebsiteReviewResponse mapToResponse(WebsiteReview review) {
        return WebsiteReviewResponse.builder()
                .id(review.getId())
                .userId(review.getUserId())
                .userName(review.getUserName())
                .userAvatar(review.getUserAvatar())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
