package com.example.backend.destinations.model;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TravelInfo {


    private List<TransportPoint> airports = new ArrayList<>();


    private List<TransportPoint> railways = new ArrayList<>();


    private List<TransportPoint> busStations = new ArrayList<>();


    @Builder.Default
    private Boolean roadConnectivity = true;

    private String roadNotes;


    private String travelTips;
}