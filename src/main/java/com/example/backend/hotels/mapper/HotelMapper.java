package com.example.backend.hotels.mapper;

import com.example.backend.hotels.dto.HotelResponse;
import com.example.backend.hotels.model.Hotel;
import com.example.backend.hotels.model.HotelImage;

import java.util.Collections;

public class HotelMapper {

    private HotelMapper() {}

    public static HotelResponse toResponse(Hotel h) {

        return HotelResponse.builder()
                .id(h.getId())
                .name(h.getName())
                .slug(h.getSlug())
                .description(h.getDescription())
                .shortDescription(h.getShortDescription())
                .hotelType(h.getHotelType())
                .destinationId(h.getDestinationId())
                .location(h.getLocation())
                .images(h.getImages() != null ? h.getImages() : Collections.emptyList())
                .coverImageUrl(extractCover(h))
                .amenities(h.getAmenities() != null ? h.getAmenities() : Collections.emptyList())
                .rooms(h.getRooms() != null ? h.getRooms() : Collections.emptyList())
                .priceRange(h.getPriceRange())
                .averageNightPrice(h.getAverageNightPrice())
                .taxIncluded(h.getTaxIncluded())
                .extraGuestPrice(h.getExtraGuestPrice())
                .policy(h.getPolicy())
                .starRating(h.getStarRating())
                .averageRating(h.getAverageRating() != null ? h.getAverageRating() : 0.0)
                .totalReviews(h.getTotalReviews() != null ? h.getTotalReviews() : 0)
                .adminAverageRating(h.getAdminAverageRating() != null ? h.getAdminAverageRating() : 0.0)
                .adminTotalReviews(h.getAdminTotalReviews() != null ? h.getAdminTotalReviews() : 0)
                .nearestAirport(h.getNearestAirport())
                .nearestRailway(h.getNearestRailway())
                .nearestBusStop(h.getNearestBusStop())
                .nearbyPlaces(h.getNearbyPlaces() != null ? h.getNearbyPlaces() : Collections.emptyList())
                .restaurantAvailable(h.getRestaurantAvailable())
                .breakfastIncluded(h.getBreakfastIncluded())
                .mealOptions(h.getMealOptions())
                .cuisineTypes(h.getCuisineTypes())
                .isActive(h.getIsActive())
                .isFullyBooked(h.getIsFullyBooked())
                .seasonalAvailability(h.getSeasonalAvailability())
                .isFeatured(h.getIsFeatured())
                .isTrending(h.getIsTrending())
                .popularityScore(h.getPopularityScore() != null ? h.getPopularityScore() : 0)
                .phone(h.getPhone())
                .email(h.getEmail())
                .website(h.getWebsite())
                .whatsapp(h.getWhatsapp())
                .safety(h.getSafety())
                .metaTitle(h.getMetaTitle() != null ? h.getMetaTitle() : h.getName())
                .metaDescription(h.getMetaDescription() != null ? h.getMetaDescription() : h.getShortDescription())
                .tags(h.getTags() != null ? h.getTags() : Collections.emptyList())
                .createdBy(h.getCreatedBy())
                .createdAt(h.getCreatedAt())
                .updatedAt(h.getUpdatedAt())
                .build();
    }

    private static String extractCover(Hotel h) {
        if (h.getImages() == null || h.getImages().isEmpty()) return null;
        return h.getImages().stream()
                .filter(img -> Boolean.TRUE.equals(img.getIsCover()))
                .map(HotelImage::getUrl)
                .findFirst()
                .orElse(h.getImages().get(0).getUrl());
    }
}
