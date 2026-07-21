package com.example.backend.destinations.dto;

import com.example.backend.destinations.model.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DestinationRequest {

    @NotBlank(message = "Destination name is required")
    @Size(max = 120, message = "Name cannot exceed 120 characters")
    private String name;

    @Size(max = 2000, message = "Description too long")
    private String description;

    @NotNull(message = "Location is required")
    private Location location;

    @NotEmpty(message = "At least one category is required")
    private List<String> categoryIds = new ArrayList<>();

    private List<@Size(max = 30, message = "Tag too long") String> tags = new ArrayList<>();

    private List<Image> images = new ArrayList<>();

    private BestTime bestTimeToVisit;

    private Weather weatherInfo;

    private TravelInfo travelInfo;

    private RouteMetadata routeMetadata;

    private List<Activity> activities = new ArrayList<>();

    @Pattern(
            regexp = "LOW|MEDIUM|HIGH",
            message = "Cost level must be LOW, MEDIUM or HIGH"
    )
    private String costLevel;

    private Boolean isFeatured = false;
}