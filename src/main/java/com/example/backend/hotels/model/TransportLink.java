package com.example.backend.hotels.model;

import lombok.*;

/**
 * Distance information for an airport, railway station, or bus stop near the hotel.
 */
@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class TransportLink {
    private String name;           // "Bhuntar Airport", "Manali Bus Stand"
    private Double distanceKm;
    private String travelTime;     // "45 min", "2 hrs"
    private Boolean isPrimary;
}
