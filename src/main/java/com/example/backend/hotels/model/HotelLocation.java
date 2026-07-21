package com.example.backend.hotels.model;

import lombok.*;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class HotelLocation {
    private String address;
    private String landmark;
    private String city;
    private String state;
    private String country;
    private String pincode;
    private Double latitude;
    private Double longitude;
}
