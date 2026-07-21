package com.example.backend.destinations.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransportPoint {

    @NotBlank(message = "Name is required")
    private String name;

    private String code;


    @PositiveOrZero
    private Double distanceKm;

    private String travelTime;


    private Boolean isPrimary;


    private String notes;

}