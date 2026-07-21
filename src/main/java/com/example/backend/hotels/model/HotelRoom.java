package com.example.backend.hotels.model;

import lombok.*;

import java.util.List;

/**
 * A single room type within a hotel.
 * One hotel can contain multiple room types (Deluxe, Suite, Dormitory, etc.)
 */
@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class HotelRoom {

    private String name;               // Deluxe Room, Suite, Family Room…
    private String description;

    private Integer maxGuests;

    /**
     * Bed types: Single, Double, Twin, King, Queen, Bunk
     */
    private String bedType;

    private String roomSize;           // e.g. "25 sqm"

    private Double basePrice;          // INR per night
    private Double discountedPrice;

    private Integer totalRooms;
    private Integer availableRooms;

    /** Room-level amenities list (e.g. "AC", "TV", "Balcony") */
    private List<String> amenities;

    /** Room-specific images */
    private List<String> imageUrls;

    private Boolean refundable;
    private Boolean breakfastIncluded;
}
