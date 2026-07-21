package com.example.backend.destinations.dto;

import com.example.backend.destinations.model.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;


@Data
@Builder
public class DestinationResponse {

    private String id;


    private String name;
    private String slug;
    private String description;

    private Location location;


    private List<String> categoryIds;
    private List<Category> categories;

    private List<String> tags;


    private List<Image> images;
    private String thumbnail;


    private BestTime bestTimeToVisit;
    private Weather weatherInfo;
    private TravelInfo travelInfo;
    private RouteMetadata routeMetadata;
    private List<Activity> activities;


    private String costLevel;


    private Double averageRating;
    private Integer totalReviews;


    private Integer popularityScore;
    private Boolean isFeatured;


    private String createdBy;
    private String creatorType;


    private String status;

    private String metaTitle;
    private String metaDescription;


    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}