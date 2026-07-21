package com.example.backend.guider.dto;

import com.example.backend.guider.model.Guide.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GuideResponse {

    private String id;
    private String fullName;
    private String displayName;
    private String slug;
    private String bio;
    private String shortBio;
    private String profileImage;
    private String coverImage;

    private String guideType;

    private List<String> destinationIds;
    private List<String> placeIds;
    private List<String> languages;

    // Contact
    private String phone;
    private String whatsapp;
    private String email;
    private String instagram;
    private String website;

    // Availability
    private List<String> availableDays;
    private List<String> availableMonths;
    private String workingHours;
    private boolean isAvailable;

    // Pricing
    private String priceType;
    private Double basePrice;
    private String currency;
    private Double hourlyRate;
    private Double dailyRate;
    private Double groupPrice;
    private Double privateTourPrice;

    // Sub-structures
    private List<Experience> experiences;
    private TransportSupport transportSupport;
    private SafetyInfo safety;
    
    // Admin Only - conditionally populated by mapper
    private VerificationDetails verification;

    private List<String> specialties;
    private List<String> tags;

    // Review / Social
    private Double averageRating;
    private Integer reviewCount;

    private List<String> images;
    private List<String> videos;

    private Integer yearsOfExperience;
    private Integer totalTours;
    private Integer repeatCustomers;
    private boolean featuredGuide;

    private List<String> supportedActivities;
    private String difficultyLevel;

    private Integer minGroupSize;
    private Integer maxGroupSize;
    private boolean privateToursAvailable;

    private boolean mealIncluded;
    private String foodType;
    private boolean localCuisineSupport;

    private EmergencyFeatures emergency;
    private BookingConfig bookingConfig;

    // Audit Info
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
