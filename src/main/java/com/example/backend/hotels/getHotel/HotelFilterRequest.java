package com.example.backend.hotels.getHotel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelFilterRequest {
    private String search;
    private String destinationId;
    private String hotelType;
    private Integer starRating;
    private Boolean coupleFriendly;
    private Boolean breakfastIncluded;
    private Double minRating;
    private Double minPrice;
    private Double maxPrice;
    
    @Builder.Default
    private int page = 0;
    
    @Builder.Default
    private int size = 9;
    
    @Builder.Default
    private String sortBy = "popularityScore";
    
    @Builder.Default
    private String sortDirection = "desc";
}
