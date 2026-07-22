package com.example.backend.guider.guiderreviews.service;

import com.example.backend.guider.guiderreviews.dto.GuideReviewRequest;
import com.example.backend.guider.guiderreviews.dto.GuideReviewResponse;
import com.example.backend.guider.guiderreviews.model.GuideReview;
import com.example.backend.guider.guiderreviews.repository.GuideReviewRepository;
import com.example.backend.guider.service.GuideService;
import com.example.backend.user.model.User;
import com.example.backend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.backend.common.dto.ReviewStats;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GuideReviewService {

    private final GuideReviewRepository guideReviewRepository;
    private final UserService userService;
    private final GuideService guideService;

    public GuideReviewResponse addOrUpdateReview(String guideId, String userId, GuideReviewRequest req) {
        if (req.getRating() == null || req.getRating() < 1 || req.getRating() > 5) {
            throw new RuntimeException("Rating must be between 1 and 5");
        }
        if (userId == null || userId.isBlank()) {
            throw new RuntimeException("Authentication required to submit a review");
        }

        User user = userService.getById(userId);
        String displayName = (user.getName() != null && !user.getName().isBlank())
                ? user.getName() : user.getUserName();

        GuideReview review;
        Optional<GuideReview> existingOpt = guideReviewRepository.findByGuideIdAndUserId(guideId, userId);

        if (existingOpt.isPresent()) {
            review = existingOpt.get();
            review.setRating(req.getRating());
            review.setComment(req.getComment());
            review.setUserName(displayName);
            review.setUserAvatar(user.getProfileImage());
            review.setUpdatedAt(LocalDateTime.now());
        } else {
            review = GuideReview.builder()
                    .guideId(guideId)
                    .userId(userId)
                    .userName(displayName)
                    .userAvatar(user.getProfileImage())
                    .rating(req.getRating())
                    .comment(req.getComment())
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
        }

        GuideReview saved = guideReviewRepository.save(review);
        recalculateAndSaveAverageRating(guideId);

        return mapToResponse(saved);
    }

    public void deleteReview(String guideId, String userId) {
        GuideReview review = guideReviewRepository.findByGuideIdAndUserId(guideId, userId)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        guideReviewRepository.delete(review);
        recalculateAndSaveAverageRating(guideId);
    }

    public Page<GuideReviewResponse> getReviewsForGuide(String guideId, Pageable pageable) {
        return guideReviewRepository.findByGuideIdOrderByCreatedAtDesc(guideId, pageable)
                .map(this::mapToResponse);
    }

    private void recalculateAndSaveAverageRating(String guideId) {
        Optional<ReviewStats> statsOpt = guideReviewRepository.getReviewStatsByGuideId(guideId);
        double averageRating = 0.0;
        int totalReviews = 0;

        if (statsOpt.isPresent()) {
            ReviewStats stats = statsOpt.get();
            totalReviews = stats.getTotalReviews() != null ? stats.getTotalReviews() : 0;
            double avg = stats.getAverageRating() != null ? stats.getAverageRating() : 0.0;
            averageRating = Math.round(avg * 10.0) / 10.0;
        }

        guideService.updateGuideReviews(guideId, averageRating, totalReviews);
    }

    private GuideReviewResponse mapToResponse(GuideReview review) {
        return GuideReviewResponse.builder()
                .id(review.getId())
                .guideId(review.getGuideId())
                .userId(review.getUserId())
                .userName(review.getUserName())
                .userAvatar(review.getUserAvatar())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }
}
