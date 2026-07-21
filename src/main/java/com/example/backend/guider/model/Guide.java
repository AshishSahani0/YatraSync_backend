package com.example.backend.guider.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "guides")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@CompoundIndexes({
    @CompoundIndex(name = "slug_unique", def = "{'slug': 1}", unique = true),
    @CompoundIndex(name = "dest_lang_idx", def = "{'destinationIds': 1, 'languages': 1}"),
    @CompoundIndex(name = "type_avail_idx", def = "{'guideType': 1, 'isAvailable': 1}"),
    @CompoundIndex(name = "pricing_idx", def = "{'basePrice': 1, 'averageRating': 1}")
})
public class Guide {

    @Id
    private String id;

    @Indexed
    private String fullName;
    private String displayName;
    
    @Indexed(unique = true)
    private String slug;

    private String bio;
    private String shortBio;
    private String profileImage;
    private String coverImage;

    @Indexed
    private String guideType; // Local, Adventure, Trekking, Cultural, Food, Wildlife, Spiritual, City Tour, Photography, Bike Tour

    @Indexed
    private List<String> destinationIds = new ArrayList<>();
    private List<String> placeIds = new ArrayList<>();
    
    @Indexed
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
    private boolean isAvailable = true;

    // Pricing
    private String priceType; // Hourly, Half Day, Full Day, Group Tour, Private Tour
    private Double basePrice;
    private String currency = "INR";
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

    // Review / Social
    private Double averageRating = 0.0;
    private Integer reviewCount = 0;
    
    private List<String> images = new ArrayList<>();
    private List<String> videos = new ArrayList<>();

    private Integer yearsOfExperience = 0;
    private Integer totalTours = 0;
    private Integer repeatCustomers = 0;
    private boolean featuredGuide = false;

    private List<String> supportedActivities = new ArrayList<>();
    private String difficultyLevel; // Beginner, Intermediate, Advanced

    private Integer minGroupSize = 1;
    private Integer maxGroupSize = 10;
    private boolean privateToursAvailable = false;

    private boolean mealIncluded = false;
    private String foodType; // Veg, Non-Veg, Both
    private boolean localCuisineSupport = false;

    private EmergencyFeatures emergency;
    private BookingConfig bookingConfig;

    // Audit Info
    private String createdBy;
    private boolean isDeleted = false;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Experience {
        private String title;
        private String description;
        private String duration;
        private String difficulty; // Beginner, Intermediate, Advanced
        private Integer maxPeople;
        private Double price;
        private List<String> includedItems = new ArrayList<>();
        private List<String> excludedItems = new ArrayList<>();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TransportSupport {
        private boolean bikeRental = false;
        private boolean cabIncluded = false;
        private boolean pickupAvailable = false;
        private boolean selfDriveHelp = false;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SafetyInfo {
        private boolean verified = false;
        private boolean governmentLicensed = false;
        private boolean firstAidCertified = false;
        private boolean femaleFriendly = false;
        private boolean emergencySupport = false;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class VerificationDetails {
        private String governmentId;
        private String licenseNumber;
        private List<String> documents = new ArrayList<>();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EmergencyFeatures {
        private String emergencyContact;
        private boolean medicalSupport = false;
        private boolean satellitePhone = false;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BookingConfig {
        private Integer advanceBookingDays = 1;
        private boolean instantBooking = false;
        private String cancellationPolicy;
        private String refundPolicy;
    }
}
