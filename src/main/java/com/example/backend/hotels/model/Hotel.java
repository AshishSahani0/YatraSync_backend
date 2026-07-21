package com.example.backend.hotels.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Hotel document — industry-level schema with 17 sections.
 *
 * <p>Indexes are chosen to support the most common filter combinations:
 * destinationId + isActive, hotelType, starRating, amenities, priceRange, isFeatured.</p>
 */
@Document(collection = "hotels")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
@CompoundIndexes({
    @CompoundIndex(name = "slug_unique",            def = "{'slug': 1}",                      unique = true),
    @CompoundIndex(name = "dest_active_idx",        def = "{'destinationId': 1, 'isActive': 1}"),
    @CompoundIndex(name = "dest_type_idx",          def = "{'destinationId': 1, 'hotelType': 1}"),
    @CompoundIndex(name = "featured_trending_idx",  def = "{'isFeatured': 1, 'isTrending': 1}"),
    @CompoundIndex(name = "price_star_idx",         def = "{'averageNightPrice': 1, 'starRating': 1}")
})
public class Hotel {

    // ─────────────────────────────────────────────────────────────────────────
    // 1. Basic Information
    // ─────────────────────────────────────────────────────────────────────────
    @Id
    private String id;

    @Indexed
    private String name;

    @Indexed(unique = true)
    private String slug;

    private String description;
    private String shortDescription;

    /**
     * Hotel type: Hotel | Resort | Hostel | Villa | Homestay | Apartment | Camp | Cottage
     */
    @Indexed
    private String hotelType;

    // ─────────────────────────────────────────────────────────────────────────
    // 2. Destination Linking
    // ─────────────────────────────────────────────────────────────────────────
    @Indexed
    private String destinationId;         // FK → destinations._id

    // ─────────────────────────────────────────────────────────────────────────
    // 3. Location
    // ─────────────────────────────────────────────────────────────────────────
    private HotelLocation location;

    // ─────────────────────────────────────────────────────────────────────────
    // 4. Images
    // ─────────────────────────────────────────────────────────────────────────
    private List<HotelImage> images = new ArrayList<>();

    // ─────────────────────────────────────────────────────────────────────────
    // 5. Amenities (flat list — most-used filter)
    // ─────────────────────────────────────────────────────────────────────────
    @Indexed
    private List<String> amenities = new ArrayList<>();

    // ─────────────────────────────────────────────────────────────────────────
    // 6. Room System
    // ─────────────────────────────────────────────────────────────────────────
    private List<HotelRoom> rooms = new ArrayList<>();

    // ─────────────────────────────────────────────────────────────────────────
    // 7. Pricing
    // ─────────────────────────────────────────────────────────────────────────
    private String priceRange;            // e.g. "₹2,000 – ₹8,000"
    @Indexed
    private Double averageNightPrice;     // for sorting / range filters
    private Boolean taxIncluded;
    private Double extraGuestPrice;

    // ─────────────────────────────────────────────────────────────────────────
    // 8. Policies
    // ─────────────────────────────────────────────────────────────────────────
    private HotelPolicy policy;

    // ─────────────────────────────────────────────────────────────────────────
    // 9. Star Rating
    // ─────────────────────────────────────────────────────────────────────────
    @Indexed
    private Integer starRating;           // 1 – 5

    private Double averageRating = 0.0;   // user review score
    private Integer totalReviews  = 0;

    private Double adminAverageRating = 0.0;
    private Integer adminTotalReviews = 0;

    // ─────────────────────────────────────────────────────────────────────────
    // 10. Nearby Transport
    // ─────────────────────────────────────────────────────────────────────────
    private TransportLink nearestAirport;
    private TransportLink nearestRailway;
    private TransportLink nearestBusStop;

    // ─────────────────────────────────────────────────────────────────────────
    // 11. Nearby Attractions
    // ─────────────────────────────────────────────────────────────────────────
    private List<NearbyPlace> nearbyPlaces = new ArrayList<>();

    // ─────────────────────────────────────────────────────────────────────────
    // 12. Food & Dining
    // ─────────────────────────────────────────────────────────────────────────
    private Boolean restaurantAvailable;
    private Boolean breakfastIncluded;
    private List<String> mealOptions;     // e.g. ["Breakfast", "Half Board"]
    private List<String> cuisineTypes;    // e.g. ["Indian", "Continental"]

    // ─────────────────────────────────────────────────────────────────────────
    // 13. Availability
    // ─────────────────────────────────────────────────────────────────────────
    @Indexed
    private Boolean isActive = true;

    private Boolean isFullyBooked = false;

    /**
     * Seasonal availability notes, e.g. "Open Oct – June only"
     */
    private String seasonalAvailability;

    // ─────────────────────────────────────────────────────────────────────────
    // 14. Featured & Trending
    // ─────────────────────────────────────────────────────────────────────────
    @Indexed
    private Boolean isFeatured = false;

    @Indexed
    private Boolean isTrending = false;

    private Integer popularityScore = 0;

    // ─────────────────────────────────────────────────────────────────────────
    // 15. Contact
    // ─────────────────────────────────────────────────────────────────────────
    private String phone;
    private String email;
    private String website;
    private String whatsapp;

    // ─────────────────────────────────────────────────────────────────────────
    // 17. Safety
    // ─────────────────────────────────────────────────────────────────────────
    private SafetyFeatures safety;

    // ─────────────────────────────────────────────────────────────────────────
    // SEO
    // ─────────────────────────────────────────────────────────────────────────
    private String metaTitle;
    private String metaDescription;
    private List<String> tags = new ArrayList<>();

    // ─────────────────────────────────────────────────────────────────────────
    // Ownership / Audit
    // ─────────────────────────────────────────────────────────────────────────
    @Indexed
    private String createdBy;

    @Indexed
    private Boolean isDeleted = false;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
