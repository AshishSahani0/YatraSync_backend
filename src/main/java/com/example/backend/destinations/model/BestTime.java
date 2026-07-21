package com.example.backend.destinations.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BestTime {


    private Integer startMonth;
    private Integer endMonth;


    private String season;


    private String notes;


    private String highlight;


    private Boolean isPeak;

}