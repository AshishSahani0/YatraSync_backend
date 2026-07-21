package com.example.backend.hotels.hotelreview.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "hotel_reviews")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelReview {

    @Id
    private String id;

    @Indexed
    private String hotelId;

    @Indexed
    private String userId; // Nullable for anonymous reviews

    private String userName; // "Anonymous User" or actual name
    
    private String userAvatar; // Nullable

    private Integer rating; // 1 to 5 stars

    private String comment;

    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
