package com.example.backend.hotels.hotelreview.controller;

import com.example.backend.hotels.hotelreview.dto.HotelReviewRequest;
import com.example.backend.hotels.hotelreview.dto.HotelReviewResponse;
import com.example.backend.hotels.hotelreview.service.HotelReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class HotelReviewController {

    private final HotelReviewService hotelReviewService;

    /**
     * Get reviews for a hotel (Public)
     */
    @GetMapping("/api/public/hotels/{hotelId}/reviews")
    public ResponseEntity<Page<HotelReviewResponse>> getReviews(
            @PathVariable String hotelId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<HotelReviewResponse> response = hotelReviewService.getReviewsForHotel(hotelId, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Add or update review — requires authentication.
     */
    @PostMapping("/api/user/hotels/{hotelId}/reviews")
    public ResponseEntity<HotelReviewResponse> addOrUpdateReview(
            @PathVariable String hotelId,
            @RequestBody HotelReviewRequest request,
            Authentication auth
    ) {
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            return ResponseEntity.status(401).build();
        }
        String userId = auth.getName();
        HotelReviewResponse response = hotelReviewService.addOrUpdateReview(hotelId, userId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete review (Authenticated User)
     */
    @DeleteMapping("/api/user/hotels/{hotelId}/reviews")
    public ResponseEntity<Void> deleteReview(
            @PathVariable String hotelId,
            Authentication auth
    ) {
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            return ResponseEntity.status(401).build();
        }
        String userId = auth.getName();
        hotelReviewService.deleteReview(hotelId, userId);
        return ResponseEntity.ok().build();
    }
}
