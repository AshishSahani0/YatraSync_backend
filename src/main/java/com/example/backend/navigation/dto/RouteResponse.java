package com.example.backend.navigation.dto;

import com.example.backend.destinations.model.Activity;
import com.example.backend.destinations.model.RouteMetadata;
import com.example.backend.guider.dto.GuideResponse;
import com.example.backend.hotels.dto.HotelResponse;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteResponse {
    private Double distanceKm;
    private Double durationMinutes;
    private List<List<Double>> routeGeometry; // [[lng, lat], [lng, lat], ...] matching GeoJSON polyline standards
    private String transportMode;

    // Cost Estimations
    private Double estimatedFuelCostInr;
    private Double estimatedTaxiCostInr;
    private Double estimatedTollsInr;

    // Destination context
    private String destinationId;
    private String destinationName;
    private String destinationDescription;
    private RouteMetadata routeMetadata;
    private List<Activity> activities;

    // Recommendations
    private List<HotelResponse> recommendedStays;
    private List<GuideResponse> recommendedGuides;
}
