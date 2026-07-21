package com.example.backend.destinations.model;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SeasonWeather {

    private String temperature;


    private String condition;


    private String description;


    private Integer avgHigh;
    private Integer avgLow;

    private String icon;

}