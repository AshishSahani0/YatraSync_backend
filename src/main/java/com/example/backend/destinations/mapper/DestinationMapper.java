package com.example.backend.destinations.mapper;

import com.example.backend.destinations.dto.DestinationResponse;

import com.example.backend.destinations.model.Category;
import com.example.backend.destinations.model.Destination;


import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class DestinationMapper {


    public static DestinationResponse toResponse(Destination d, List<Category> categories) {

        return DestinationResponse.builder()
                .id(d.getId())


                .name(d.getName())
                .slug(d.getSlug())
                .description(d.getDescription())
                .location(d.getLocation())


                .categoryIds(safeList(d.getCategoryIds()))
                .categories(categories != null ? categories : Collections.emptyList())


                .tags(safeList(d.getTags()))


                .images(safeList(d.getImages()))
                .thumbnail(extractThumbnail(d))


                .bestTimeToVisit(d.getBestTimeToVisit())
                .weatherInfo(d.getWeatherInfo())
                .travelInfo(d.getTravelInfo())
                .routeMetadata(d.getRouteMetadata())
                .activities(safeList(d.getActivities()))


                .costLevel(d.getCostLevel())


                .averageRating(Optional.ofNullable(d.getAverageRating()).orElse(0.0))
                .totalReviews(Optional.ofNullable(d.getTotalReviews()).orElse(0))


                .popularityScore(Optional.ofNullable(d.getPopularityScore()).orElse(0))
                .isFeatured(Optional.ofNullable(d.getIsFeatured()).orElse(false))


                .createdBy(d.getCreatedBy())
                .creatorType(d.getCreatorType())


                .status(d.getStatus())


                .metaTitle(
                        d.getMetaTitle() != null ? d.getMetaTitle() : d.getName()
                )
                .metaDescription(
                        d.getMetaDescription() != null ? d.getMetaDescription() : d.getDescription()
                )


                .createdAt(d.getCreatedAt())
                .updatedAt(d.getUpdatedAt())

                .build();
    }


    public static DestinationResponse toResponse(Destination d) {
        return toResponse(d, Collections.emptyList());
    }


    private static <T> List<T> safeList(List<T> list) {
        return list != null ? list : Collections.emptyList();
    }


    private static String extractThumbnail(Destination d) {
        if (d.getImages() == null || d.getImages().isEmpty()) {
            return null;
        }
        return d.getImages().get(0).getUrl();
    }
}