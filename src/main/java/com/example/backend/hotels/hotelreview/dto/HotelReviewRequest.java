package com.example.backend.hotels.hotelreview.dto;

import lombok.Data;

@Data
public class HotelReviewRequest {
    private Integer rating;
    private String comment;
}
