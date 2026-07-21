package com.example.backend.destinations.review.service;

import com.example.backend.destinations.review.dto.ReviewRequest;
import com.example.backend.destinations.review.dto.ReviewResponse;
import com.example.backend.destinations.review.model.Review;
import com.example.backend.destinations.review.repository.ReviewRepository;
import com.example.backend.destinations.service.DestinationService;
import com.example.backend.user.model.User;
import com.example.backend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserService userService;
    private final DestinationService destinationService;

    public ReviewResponse addOrUpdateReview(String destinationId, String userId, ReviewRequest req) {
        if (req.getRating() == null || req.getRating() < 1 || req.getRating() > 5) {
            throw new RuntimeException("Rating must be between 1 and 5");
        }
        if (userId == null || userId.isBlank()) {
            throw new RuntimeException("Authentication required to submit a review");
        }

        User user = userService.getById(userId);
        String displayName = (user.getName() != null && !user.getName().isBlank())
                ? user.getName() : user.getUserName();

        Review review;
        Optional<Review> existingOpt = reviewRepository.findByDestinationIdAndUserId(destinationId, userId);

        if (existingOpt.isPresent()) {
            review = existingOpt.get();
            review.setRating(req.getRating());
            review.setComment(req.getComment());
            review.setUserName(displayName);
            review.setUserAvatar(user.getProfileImage());
            review.setUpdatedAt(LocalDateTime.now());
        } else {
            review = Review.builder()
                    .destinationId(destinationId)
                    .userId(userId)
                    .userName(displayName)
                    .userAvatar(user.getProfileImage())
                    .rating(req.getRating())
                    .comment(req.getComment())
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
        }

        Review saved = reviewRepository.save(review);
        recalculateAndSaveAverageRating(destinationId);

        return mapToResponse(saved);
    }

    public void deleteReview(String destinationId, String userId) {
        Review review = reviewRepository.findByDestinationIdAndUserId(destinationId, userId)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        reviewRepository.delete(review);
        recalculateAndSaveAverageRating(destinationId);
    }

    public Page<ReviewResponse> getReviewsForDestination(String destinationId, Pageable pageable) {
        return reviewRepository.findByDestinationIdOrderByCreatedAtDesc(destinationId, pageable)
                .map(this::mapToResponse);
    }

    private void recalculateAndSaveAverageRating(String destinationId) {
        List<Review> reviews = reviewRepository.findByDestinationId(destinationId);
        int totalReviews = reviews.size();
        double averageRating = 0.0;

        if (totalReviews > 0) {
            double sum = 0.0;
            for (Review r : reviews) {
                sum += r.getRating();
            }
            averageRating = sum / totalReviews;
            // Round to 1 decimal place
            averageRating = Math.round(averageRating * 10.0) / 10.0;
        }

        destinationService.updateDestinationReviews(destinationId, averageRating, totalReviews);
    }

    private ReviewResponse mapToResponse(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .destinationId(review.getDestinationId())
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
