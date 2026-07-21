package com.example.backend.navigation.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteRequest {

    @NotNull(message = "Source latitude is required")
    private Double sourceLat;

    @NotNull(message = "Source longitude is required")
    private Double sourceLng;

    @NotBlank(message = "Destination ID is required")
    private String destinationId;

    private String transportMode; // driving | walking | cycling
    private String sourceName;
}
