package com.example.backend.destinations.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteMetadata {
    private String bestRoadRoute;
    private String nearestAirport;
    private String nearestRailway;
    private Boolean permitRequired;
    private String roadDifficulty; // Easy | Moderate | Demanding | Extreme
}
