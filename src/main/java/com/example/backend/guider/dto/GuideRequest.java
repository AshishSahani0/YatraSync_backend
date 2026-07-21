package com.example.backend.guider.dto;

import com.example.backend.guider.model.Guide.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GuideRequest {

    private String fullName;
    private String displayName;
    private String bio;
    private String shortBio;
    private String profileImage;
    private String coverImage;

    private String guideType;

    private List<String> destinationIds = new ArrayList<>();
    private List<String> placeIds = new ArrayList<>();
    private List<String> languages = new ArrayList<>();

    // Contact
    private String phone;
    private String whatsapp;
    private String email;
    private String instagram;
    private String website;

    // Availability
    private List<String> availableDays = new ArrayList<>();
    private List<String> availableMonths = new ArrayList<>();
    private String workingHours;
    private Boolean isAvailable;

    // Pricing
    private String priceType;
    private Double basePrice;
    private String currency;
    private Double hourlyRate;
    private Double dailyRate;
    private Double groupPrice;
    private Double privateTourPrice;

    // Sub-structures
    private List<Experience> experiences = new ArrayList<>();
    private TransportSupport transportSupport;
    private SafetyInfo safety;
    private VerificationDetails verification;

    private List<String> specialties = new ArrayList<>();
    private List<String> tags = new ArrayList<>();

    private List<String> images = new ArrayList<>();
    private List<String> videos = new ArrayList<>();

    private Integer yearsOfExperience;
    private Integer totalTours;
    private Integer repeatCustomers;
    private Boolean featuredGuide;

    private List<String> supportedActivities = new ArrayList<>();
    private String difficultyLevel;

    private Integer minGroupSize;
    private Integer maxGroupSize;
    private Boolean privateToursAvailable;

    private Boolean mealIncluded;
    private String foodType;
    private Boolean localCuisineSupport;

    private EmergencyFeatures emergency;
    private BookingConfig bookingConfig;
}
