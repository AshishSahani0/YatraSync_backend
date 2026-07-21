package com.example.backend.hotels.model;

import lombok.*;

/**
 * Safety and security features of the hotel.
 */
@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class SafetyFeatures {
    private Boolean cctv;
    private Boolean security24x7;
    private Boolean fireSafety;
    private Boolean femaleFriendly;
    private Boolean firstAidAvailable;
    private Boolean sanitizationProtocol;
}
