package com.example.backend.destinations.getDestination;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DestinationFilterRequest {
    private String search;
    private List<String> categoryIds;
    private String costLevel;
    private Boolean isFeatured;
    private String season;
    private Double minRating;
    
    @Builder.Default
    private int page = 0;
    
    @Builder.Default
    private int size = 10;
    
    @Builder.Default
    private String sortBy = "popularityScore";
    
    @Builder.Default
    private String sortDirection = "desc";
}
