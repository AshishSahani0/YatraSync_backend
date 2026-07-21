package com.example.backend.destinations.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "categories")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @Id
    private String id;


    @Indexed(unique = true)
    private String name;

    @Indexed(unique = true)
    private String slug;


    private String icon;
    private String imageUrl;
    private String description;


    @Indexed
    private Boolean isActive = true;

    private Integer priority = 0;



    private String metaTitle;
    private String metaDescription;


    private String createdBy;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}