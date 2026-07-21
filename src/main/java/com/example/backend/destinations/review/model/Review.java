package com.example.backend.destinations.review.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "destination_reviews")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {

    @Id
    private String id;

    @Indexed
    private String destinationId;

    @Indexed
    private String userId; // Nullable for anonymous reviews

    private String userName; // "Anonymous User" or actual name
    
    private String userAvatar; // Nullable

    private Integer rating; // 1 to 5 stars

    private String comment;

    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
