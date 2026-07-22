package com.example.backend.destinations.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "destinations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@CompoundIndexes({
        @CompoundIndex(name = "slug_unique", def = "{'slug': 1}", unique = true),
        @CompoundIndex(name = "status_deleted_idx", def = "{'status': 1, 'isDeleted': 1}")
})
public class Destination {

    @Id
    private String id;


    @Indexed
    @TextIndexed
    private String name;

    @Indexed(unique = true)
    private String slug;

    @TextIndexed
    private String description;


    private Location location;


    @Indexed
    private List<String> categoryIds = new ArrayList<>();

    @Indexed
    private List<String> tags = new ArrayList<>();


    private List<Image> images = new ArrayList<>();


    private BestTime bestTimeToVisit;
    private Weather weatherInfo;
    private TravelInfo travelInfo;
    private RouteMetadata routeMetadata;
    private List<Activity> activities = new ArrayList<>();


    @Indexed
    private String costLevel;


    private Double averageRating = 0.0;
    private Integer totalReviews = 0;


    @Indexed
    private Integer popularityScore = 0;

    @Indexed
    private Boolean isFeatured = false;


    @Indexed
    private String createdBy;

    private String creatorType; // ADMIN | GUIDE


    @Indexed
    private String status;

    @Indexed
    private Boolean isDeleted = false;


    private String metaTitle;
    private String metaDescription;


    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}