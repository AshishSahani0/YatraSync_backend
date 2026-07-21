package com.example.backend.destinations.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Activity {


    private String name;
    private String description;


    private String type;



    private String priceRange;



    private String duration;


    private String bestTime;


    private Boolean isPopular;


    private String imageUrl;
}