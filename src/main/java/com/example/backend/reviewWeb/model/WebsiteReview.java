package com.example.backend.reviewWeb.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "website_reviews")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WebsiteReview {

    @Id
    private String id;

    @Indexed
    private String userId; // Nullable for anonymous reviews

    private String userName; // "Anonymous" or actual display name

    private String userAvatar; // Nullable

    private Integer rating; // 1 to 5 stars

    private String comment; // Review text feedback

    @Indexed
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
