package com.example.backend.hotels.model;

import lombok.*;

/**
 * A nearby tourist place or attraction from the hotel.
 */
@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class NearbyPlace {
    private String name;           // "Solang Valley", "Mall Road"
    private Double distanceKm;
    private String travelTime;     // "30 min"
    private String category;       // "Adventure", "Shopping", "Sightseeing"
}
