package com.example.backend.navigation.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "route_history")
@CompoundIndexes({
        @CompoundIndex(name = "user_searched_at_idx", def = "{'userId': 1, 'searchedAt': -1}")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteHistory {

    @Id
    private String id;

    @Indexed
    private String userId; // Nullable if search is anonymous

    private String sourceName;
    private Double sourceLat;
    private Double sourceLng;

    @Indexed
    private String destinationId;
    private String destinationName;

    private Double distanceKm;
    private Double durationMinutes;
    private String transportMode;

    @Indexed
    private LocalDateTime searchedAt;
}
