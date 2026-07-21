package com.example.backend.hotels.model;

import lombok.*;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class HotelImage {
    private String url;
    private String caption;
    private Boolean isCover;
    private Boolean isRoomImage;

    /**
     * Image category examples: Exterior, Lobby, Deluxe Room, Bathroom,
     * Pool, Restaurant, View, Amenities
     */
    private String category;
    private Integer order;
}
