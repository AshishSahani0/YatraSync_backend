package com.example.backend.hotels.hotelreview.service;

import com.example.backend.hotels.hotelreview.dto.HotelReviewRequest;
import com.example.backend.hotels.hotelreview.dto.HotelReviewResponse;
import com.example.backend.hotels.hotelreview.model.HotelReview;
import com.example.backend.hotels.hotelreview.repository.HotelReviewRepository;
import com.example.backend.hotels.service.HotelService;
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
public class HotelReviewService {

    private final HotelReviewRepository hotelReviewRepository;
    private final UserService userService;
    private final HotelService hotelService;

    public HotelReviewResponse addOrUpdateReview(String hotelId, String userId, HotelReviewRequest req) {
        if (req.getRating() == null || req.getRating() < 1 || req.getRating() > 5) {
            throw new RuntimeException("Rating must be between 1 and 5");
        }
        if (userId == null || userId.isBlank()) {
            throw new RuntimeException("Authentication required to submit a review");
        }

        User user = userService.getById(userId);
        String displayName = (user.getName() != null && !user.getName().isBlank())
                ? user.getName() : user.getUserName();

        HotelReview review;
        Optional<HotelReview> existingOpt = hotelReviewRepository.findByHotelIdAndUserId(hotelId, userId);

        if (existingOpt.isPresent()) {
            review = existingOpt.get();
            review.setRating(req.getRating());
            review.setComment(req.getComment());
            review.setUserName(displayName);
            review.setUserAvatar(user.getProfileImage());
            review.setUpdatedAt(LocalDateTime.now());
        } else {
            review = HotelReview.builder()
                    .hotelId(hotelId)
                    .userId(userId)
                    .userName(displayName)
                    .userAvatar(user.getProfileImage())
                    .rating(req.getRating())
                    .comment(req.getComment())
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
        }

        HotelReview saved = hotelReviewRepository.save(review);
        recalculateAndSaveAverageRating(hotelId);

        return mapToResponse(saved);
    }

    public void deleteReview(String hotelId, String userId) {
        HotelReview review = hotelReviewRepository.findByHotelIdAndUserId(hotelId, userId)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        hotelReviewRepository.delete(review);
        recalculateAndSaveAverageRating(hotelId);
    }

    public Page<HotelReviewResponse> getReviewsForHotel(String hotelId, Pageable pageable) {
        return hotelReviewRepository.findByHotelIdOrderByCreatedAtDesc(hotelId, pageable)
                .map(this::mapToResponse);
    }

    private void recalculateAndSaveAverageRating(String hotelId) {
        hotelService.recalculateAndSaveAverageRating(hotelId);
    }

    private HotelReviewResponse mapToResponse(HotelReview review) {
        return HotelReviewResponse.builder()
                .id(review.getId())
                .hotelId(review.getHotelId())
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
