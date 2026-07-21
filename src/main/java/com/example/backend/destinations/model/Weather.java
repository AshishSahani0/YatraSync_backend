package com.example.backend.destinations.model;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Weather {


    private SeasonWeather summer;
    private SeasonWeather winter;
    private SeasonWeather monsoon;
    private SeasonWeather spring;


    private String summary;


    private String bestMonths;

}