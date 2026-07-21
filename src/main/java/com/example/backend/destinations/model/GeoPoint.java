package com.example.backend.destinations.model;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GeoPoint {

    @Builder.Default
    private String type = "Point";

    @NotNull(message = "Coordinates are required")
    @Size(min = 2, max = 2, message = "Coordinates must be [lng, lat]")
    private List<
            @NotNull
            @DecimalMin(value = "-180.0")
            @DecimalMax(value = "180.0")
                    Double
            > coordinates;
}