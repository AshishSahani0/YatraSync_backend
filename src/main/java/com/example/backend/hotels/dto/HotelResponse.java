package com.example.backend.hotels.dto;

import com.example.backend.hotels.model.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Outgoing hotel response — mirrors the Hotel entity but adds computed fields
 * such as {@code coverImageUrl} (pre-extracted for fast rendering).
 */
@Data @Builder
public class HotelResponse {

    private String id;
    private String name;
    private String slug;
    private String description;
    private String shortDescription;
    private String hotelType;

    // Destination linkage
    private String destinationId;

    // Location
    private HotelLocation location;

    // Images
    private List<HotelImage> images;
    private String coverImageUrl;          // pre-extracted for card rendering

    // Amenities
    private List<String> amenities;

    // Rooms
    private List<HotelRoom> rooms;

    // Pricing
    private String priceRange;
    private Double averageNightPrice;
    private Boolean taxIncluded;
    private Double extraGuestPrice;

    // Policy
    private HotelPolicy policy;

    // Ratings
    private Integer starRating;
    private Double averageRating;
    private Integer totalReviews;
    private Double adminAverageRating;
    private Integer adminTotalReviews;

    // Transport
    private TransportLink nearestAirport;
    private TransportLink nearestRailway;
    private TransportLink nearestBusStop;

    // Nearby
    private List<NearbyPlace> nearbyPlaces;

    // Food
    private Boolean restaurantAvailable;
    private Boolean breakfastIncluded;
    private List<String> mealOptions;
    private List<String> cuisineTypes;

    // Availability
    private Boolean isActive;
    private Boolean isFullyBooked;
    private String seasonalAvailability;

    // Discovery
    private Boolean isFeatured;
    private Boolean isTrending;
    private Integer popularityScore;

    // Contact
    private String phone;
    private String email;
    private String website;
    private String whatsapp;

    // Safety
    private SafetyFeatures safety;

    // SEO
    private String metaTitle;
    private String metaDescription;
    private List<String> tags;

    // Audit
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
