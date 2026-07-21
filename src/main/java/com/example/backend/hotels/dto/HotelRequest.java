package com.example.backend.hotels.dto;

import com.example.backend.hotels.model.*;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

/**
 * Incoming request body for creating or updating a Hotel.
 * All optional fields can be null; the service handles null-safe merging on updates.
 */
@Data
public class HotelRequest {

    // ── 1. Basic ─────────────────────────────────────────────────────────────
    @NotBlank(message = "Hotel name is required")
    private String name;

    private String description;
    private String shortDescription;

    /** Hotel | Resort | Hostel | Villa | Homestay | Apartment | Camp | Cottage */
    private String hotelType;

    // ── 2. Destination Link ───────────────────────────────────────────────────
    @NotBlank(message = "Destination ID is required")
    private String destinationId;

    // ── 3. Location ───────────────────────────────────────────────────────────
    private HotelLocation location;

    // ── 4. Images ─────────────────────────────────────────────────────────────
    private List<HotelImage> images;

    // ── 5. Amenities ──────────────────────────────────────────────────────────
    private List<String> amenities;

    // ── 6. Rooms ──────────────────────────────────────────────────────────────
    private List<HotelRoom> rooms;

    // ── 7. Pricing ────────────────────────────────────────────────────────────
    private String priceRange;
    private Double averageNightPrice;
    private Boolean taxIncluded;
    private Double extraGuestPrice;

    // ── 8. Policy ─────────────────────────────────────────────────────────────
    private HotelPolicy policy;

    // ── 9. Star Rating ────────────────────────────────────────────────────────
    private Integer starRating;
    private Double adminAverageRating;
    private Integer adminTotalReviews;

    // ── 10. Transport Links ───────────────────────────────────────────────────
    private TransportLink nearestAirport;
    private TransportLink nearestRailway;
    private TransportLink nearestBusStop;

    // ── 11. Nearby Places ─────────────────────────────────────────────────────
    private List<NearbyPlace> nearbyPlaces;

    // ── 12. Food & Dining ─────────────────────────────────────────────────────
    private Boolean restaurantAvailable;
    private Boolean breakfastIncluded;
    private List<String> mealOptions;
    private List<String> cuisineTypes;

    // ── 13. Availability ──────────────────────────────────────────────────────
    private Boolean isActive;
    private Boolean isFullyBooked;
    private String seasonalAvailability;

    // ── 14. Featured & Trending ───────────────────────────────────────────────
    private Boolean isFeatured;
    private Boolean isTrending;

    // ── 15. Contact ───────────────────────────────────────────────────────────
    private String phone;
    private String email;
    private String website;
    private String whatsapp;

    // ── 17. Safety ────────────────────────────────────────────────────────────
    private SafetyFeatures safety;

    // ── SEO / Tags ────────────────────────────────────────────────────────────
    private String metaTitle;
    private String metaDescription;
    private List<String> tags;
}
