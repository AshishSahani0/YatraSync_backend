package com.example.backend.guider.mapper;

import com.example.backend.guider.dto.GuideResponse;
import com.example.backend.guider.model.Guide;

import java.util.Collections;

public class GuideMapper {

    private GuideMapper() {}

    /**
     * Map model to public response - Strips out government ID and document links
     */
    public static GuideResponse toResponse(Guide g) {
        if (g == null) return null;
        
        GuideResponse res = toResponseBase(g);
        res.setVerification(null); // Ensure strict boundary security for public
        return res;
    }

    /**
     * Map model to admin response - Includes sensitive verification documents
     */
    public static GuideResponse toAdminResponse(Guide g) {
        if (g == null) return null;

        GuideResponse res = toResponseBase(g);
        res.setVerification(g.getVerification());
        return res;
    }

    private static GuideResponse toResponseBase(Guide g) {
        return GuideResponse.builder()
                .id(g.getId())
                .fullName(g.getFullName())
                .displayName(g.getDisplayName() != null ? g.getDisplayName() : g.getFullName())
                .slug(g.getSlug())
                .bio(g.getBio())
                .shortBio(g.getShortBio())
                .profileImage(g.getProfileImage())
                .coverImage(g.getCoverImage())
                .guideType(g.getGuideType())
                .destinationIds(g.getDestinationIds() != null ? g.getDestinationIds() : Collections.emptyList())
                .placeIds(g.getPlaceIds() != null ? g.getPlaceIds() : Collections.emptyList())
                .languages(g.getLanguages() != null ? g.getLanguages() : Collections.emptyList())
                .phone(g.getPhone())
                .whatsapp(g.getWhatsapp())
                .email(g.getEmail())
                .instagram(g.getInstagram())
                .website(g.getWebsite())
                .availableDays(g.getAvailableDays() != null ? g.getAvailableDays() : Collections.emptyList())
                .availableMonths(g.getAvailableMonths() != null ? g.getAvailableMonths() : Collections.emptyList())
                .workingHours(g.getWorkingHours())
                .isAvailable(g.isAvailable())
                .priceType(g.getPriceType())
                .basePrice(g.getBasePrice())
                .currency(g.getCurrency() != null ? g.getCurrency() : "INR")
                .hourlyRate(g.getHourlyRate())
                .dailyRate(g.getDailyRate())
                .groupPrice(g.getGroupPrice())
                .privateTourPrice(g.getPrivateTourPrice())
                .experiences(g.getExperiences() != null ? g.getExperiences() : Collections.emptyList())
                .transportSupport(g.getTransportSupport())
                .safety(g.getSafety())
                .specialties(g.getSpecialties() != null ? g.getSpecialties() : Collections.emptyList())
                .tags(g.getTags() != null ? g.getTags() : Collections.emptyList())
                .averageRating(g.getAverageRating() != null ? g.getAverageRating() : 0.0)
                .reviewCount(g.getReviewCount() != null ? g.getReviewCount() : 0)
                .images(g.getImages() != null ? g.getImages() : Collections.emptyList())
                .videos(g.getVideos() != null ? g.getVideos() : Collections.emptyList())
                .yearsOfExperience(g.getYearsOfExperience() != null ? g.getYearsOfExperience() : 0)
                .totalTours(g.getTotalTours() != null ? g.getTotalTours() : 0)
                .repeatCustomers(g.getRepeatCustomers() != null ? g.getRepeatCustomers() : 0)
                .featuredGuide(g.isFeaturedGuide())
                .supportedActivities(g.getSupportedActivities() != null ? g.getSupportedActivities() : Collections.emptyList())
                .difficultyLevel(g.getDifficultyLevel())
                .minGroupSize(g.getMinGroupSize() != null ? g.getMinGroupSize() : 1)
                .maxGroupSize(g.getMaxGroupSize() != null ? g.getMaxGroupSize() : 10)
                .privateToursAvailable(g.isPrivateToursAvailable())
                .mealIncluded(g.isMealIncluded())
                .foodType(g.getFoodType())
                .localCuisineSupport(g.isLocalCuisineSupport())
                .emergency(g.getEmergency())
                .bookingConfig(g.getBookingConfig())
                .createdBy(g.getCreatedBy())
                .createdAt(g.getCreatedAt())
                .updatedAt(g.getUpdatedAt())
                .build();
    }
}
