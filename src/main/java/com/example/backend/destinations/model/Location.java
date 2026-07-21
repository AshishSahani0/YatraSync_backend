package com.example.backend.destinations.model;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Location {


    @NotBlank(message = "Country is required")
    private String country;

    private String state;
    private String city;


    private String displayName;


    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private GeoPoint geoPoint;


    private Double latitude;
    private Double longitude;
}