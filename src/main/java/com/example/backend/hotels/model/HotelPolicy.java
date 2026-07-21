package com.example.backend.hotels.model;

import lombok.*;

/**
 * Hotel policy settings — check-in/out times, guest restrictions, cancellation.
 */
@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class HotelPolicy {
    private String checkInTime;                  // "12:00 PM"
    private String checkOutTime;                 // "11:00 AM"

    private Boolean coupleFriendly;
    private Boolean petFriendly;
    private Boolean smokingAllowed;
    private Boolean unmarriedCouplesAllowed;
    private Boolean localIdAccepted;

    /**
     * Cancellation policy description.
     * Examples: "Free cancellation before 24 hrs", "Non-refundable"
     */
    private String cancellationPolicy;

    /** Additional house rules text */
    private String houseRules;
    private String guestRestrictions;
    private String quietHours;                   // e.g. "10 PM – 6 AM"
}
