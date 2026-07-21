package com.example.backend.guider.service;

import com.example.backend.destinations.getDestination.PageResponse;
import com.example.backend.guider.dto.GuideRequest;
import com.example.backend.guider.dto.GuideResponse;
import com.example.backend.guider.mapper.GuideMapper;
import com.example.backend.guider.model.Guide;
import com.example.backend.guider.repository.GuideRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GuideService {

    private final GuideRepository guideRepository;
    private final MongoTemplate mongoTemplate;

    // ─── CREATE ──────────────────────────────────────────────────────────────

    public GuideResponse createGuide(GuideRequest req, String adminId) {
        if (req.getFullName() == null || req.getFullName().isBlank()) {
            throw new RuntimeException("Full name is required");
        }

        String slug = generateUniqueSlug(req.getFullName());

        Guide guide = Guide.builder()
                .fullName(req.getFullName().trim())
                .displayName(req.getDisplayName() != null ? req.getDisplayName().trim() : req.getFullName().trim())
                .slug(slug)
                .bio(req.getBio())
                .shortBio(req.getShortBio())
                .profileImage(req.getProfileImage())
                .coverImage(req.getCoverImage())
                .guideType(req.getGuideType())
                .destinationIds(safeList(req.getDestinationIds()))
                .placeIds(safeList(req.getPlaceIds()))
                .languages(safeList(req.getLanguages()))
                .phone(req.getPhone())
                .whatsapp(req.getWhatsapp())
                .email(req.getEmail())
                .instagram(req.getInstagram())
                .website(req.getWebsite())
                .availableDays(safeList(req.getAvailableDays()))
                .availableMonths(safeList(req.getAvailableMonths()))
                .workingHours(req.getWorkingHours())
                .isAvailable(req.getIsAvailable() != null ? req.getIsAvailable() : true)
                .priceType(req.getPriceType())
                .basePrice(req.getBasePrice())
                .currency(req.getCurrency() != null ? req.getCurrency() : "INR")
                .hourlyRate(req.getHourlyRate())
                .dailyRate(req.getDailyRate())
                .groupPrice(req.getGroupPrice())
                .privateTourPrice(req.getPrivateTourPrice())
                .experiences(req.getExperiences() != null ? req.getExperiences() : new ArrayList<>())
                .transportSupport(req.getTransportSupport())
                .safety(req.getSafety())
                .verification(req.getVerification())
                .specialties(safeList(req.getSpecialties()))
                .tags(safeList(req.getTags()))
                .averageRating(0.0)
                .reviewCount(0)
                .images(safeList(req.getImages()))
                .videos(safeList(req.getVideos()))
                .yearsOfExperience(req.getYearsOfExperience() != null ? req.getYearsOfExperience() : 0)
                .totalTours(req.getTotalTours() != null ? req.getTotalTours() : 0)
                .repeatCustomers(req.getRepeatCustomers() != null ? req.getRepeatCustomers() : 0)
                .featuredGuide(req.getFeaturedGuide() != null ? req.getFeaturedGuide() : false)
                .supportedActivities(safeList(req.getSupportedActivities()))
                .difficultyLevel(req.getDifficultyLevel())
                .minGroupSize(req.getMinGroupSize() != null ? req.getMinGroupSize() : 1)
                .maxGroupSize(req.getMaxGroupSize() != null ? req.getMaxGroupSize() : 10)
                .privateToursAvailable(req.getPrivateToursAvailable() != null ? req.getPrivateToursAvailable() : false)
                .mealIncluded(req.getMealIncluded() != null ? req.getMealIncluded() : false)
                .foodType(req.getFoodType())
                .localCuisineSupport(req.getLocalCuisineSupport() != null ? req.getLocalCuisineSupport() : false)
                .emergency(req.getEmergency())
                .bookingConfig(req.getBookingConfig())
                .createdBy(adminId)
                .isDeleted(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return GuideMapper.toAdminResponse(guideRepository.save(guide));
    }

    // ─── UPDATE ──────────────────────────────────────────────────────────────

    public GuideResponse updateGuide(String id, GuideRequest req, String adminId) {
        Guide guide = guideRepository.findById(id)
                .filter(g -> !g.isDeleted())
                .orElseThrow(() -> new RuntimeException("Guide not found"));

        if (req.getFullName() != null && !req.getFullName().isBlank()) {
            String newName = req.getFullName().trim();
            if (!newName.equals(guide.getFullName())) {
                guide.setFullName(newName);
                guide.setSlug(generateUniqueSlug(newName));
            }
        }
        if (req.getDisplayName() != null) guide.setDisplayName(req.getDisplayName().trim());
        if (req.getBio() != null) guide.setBio(req.getBio());
        if (req.getShortBio() != null) guide.setShortBio(req.getShortBio());
        if (req.getProfileImage() != null) guide.setProfileImage(req.getProfileImage());
        if (req.getCoverImage() != null) guide.setCoverImage(req.getCoverImage());
        if (req.getGuideType() != null) guide.setGuideType(req.getGuideType());
        if (req.getDestinationIds() != null) guide.setDestinationIds(req.getDestinationIds());
        if (req.getPlaceIds() != null) guide.setPlaceIds(req.getPlaceIds());
        if (req.getLanguages() != null) guide.setLanguages(req.getLanguages());
        if (req.getPhone() != null) guide.setPhone(req.getPhone());
        if (req.getWhatsapp() != null) guide.setWhatsapp(req.getWhatsapp());
        if (req.getEmail() != null) guide.setEmail(req.getEmail());
        if (req.getInstagram() != null) guide.setInstagram(req.getInstagram());
        if (req.getWebsite() != null) guide.setWebsite(req.getWebsite());
        if (req.getAvailableDays() != null) guide.setAvailableDays(req.getAvailableDays());
        if (req.getAvailableMonths() != null) guide.setAvailableMonths(req.getAvailableMonths());
        if (req.getWorkingHours() != null) guide.setWorkingHours(req.getWorkingHours());
        if (req.getIsAvailable() != null) guide.setAvailable(req.getIsAvailable());
        if (req.getPriceType() != null) guide.setPriceType(req.getPriceType());
        if (req.getBasePrice() != null) guide.setBasePrice(req.getBasePrice());
        if (req.getCurrency() != null) guide.setCurrency(req.getCurrency());
        if (req.getHourlyRate() != null) guide.setHourlyRate(req.getHourlyRate());
        if (req.getDailyRate() != null) guide.setDailyRate(req.getDailyRate());
        if (req.getGroupPrice() != null) guide.setGroupPrice(req.getGroupPrice());
        if (req.getPrivateTourPrice() != null) guide.setPrivateTourPrice(req.getPrivateTourPrice());
        if (req.getExperiences() != null) guide.setExperiences(req.getExperiences());
        if (req.getTransportSupport() != null) guide.setTransportSupport(req.getTransportSupport());
        if (req.getSafety() != null) guide.setSafety(req.getSafety());
        if (req.getVerification() != null) guide.setVerification(req.getVerification());
        if (req.getSpecialties() != null) guide.setSpecialties(req.getSpecialties());
        if (req.getTags() != null) guide.setTags(req.getTags());
        if (req.getImages() != null) guide.setImages(req.getImages());
        if (req.getVideos() != null) guide.setVideos(req.getVideos());
        if (req.getYearsOfExperience() != null) guide.setYearsOfExperience(req.getYearsOfExperience());
        if (req.getTotalTours() != null) guide.setTotalTours(req.getTotalTours());
        if (req.getRepeatCustomers() != null) guide.setRepeatCustomers(req.getRepeatCustomers());
        if (req.getFeaturedGuide() != null) guide.setFeaturedGuide(req.getFeaturedGuide());
        if (req.getSupportedActivities() != null) guide.setSupportedActivities(req.getSupportedActivities());
        if (req.getDifficultyLevel() != null) guide.setDifficultyLevel(req.getDifficultyLevel());
        if (req.getMinGroupSize() != null) guide.setMinGroupSize(req.getMinGroupSize());
        if (req.getMaxGroupSize() != null) guide.setMaxGroupSize(req.getMaxGroupSize());
        if (req.getPrivateToursAvailable() != null) guide.setPrivateToursAvailable(req.getPrivateToursAvailable());
        if (req.getMealIncluded() != null) guide.setMealIncluded(req.getMealIncluded());
        if (req.getFoodType() != null) guide.setFoodType(req.getFoodType());
        if (req.getLocalCuisineSupport() != null) guide.setLocalCuisineSupport(req.getLocalCuisineSupport());
        if (req.getEmergency() != null) guide.setEmergency(req.getEmergency());
        if (req.getBookingConfig() != null) guide.setBookingConfig(req.getBookingConfig());

        guide.setUpdatedAt(LocalDateTime.now());
        return GuideMapper.toAdminResponse(guideRepository.save(guide));
    }

    // ─── READ ────────────────────────────────────────────────────────────────

    public Page<GuideResponse> getAllForAdmin(Pageable pageable) {
        Pageable safe = pageable.getSort().isSorted() ? pageable : PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("createdAt").descending());
        return guideRepository.findByIsDeletedFalse(safe)
                .map(GuideMapper::toAdminResponse);
    }

    public GuideResponse getByIdForAdmin(String id) {
        Guide guide = guideRepository.findById(id)
                .filter(g -> !g.isDeleted())
                .orElseThrow(() -> new RuntimeException("Guide not found"));
        return GuideMapper.toAdminResponse(guide);
    }

    public GuideResponse getBySlugPublic(String slug) {
        Guide guide = guideRepository.findBySlug(slug)
                .filter(g -> !g.isDeleted())
                .filter(Guide::isAvailable)
                .orElseThrow(() -> new RuntimeException("Guide not found or currently unavailable"));
        return GuideMapper.toResponse(guide);
    }

    // ─── SOFT DELETE ─────────────────────────────────────────────────────────

    public void deleteGuide(String id) {
        Guide guide = guideRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Guide not found"));
        guide.setDeleted(true);
        guide.setAvailable(false);
        guide.setUpdatedAt(LocalDateTime.now());
        guideRepository.save(guide);
    }

    // ─── DYNAMIC EXPLORE FILTRATION ──────────────────────────────────────────

    public PageResponse<Guide> getFilteredGuides(
            String search,
            List<String> destinationIds,
            List<String> languages,
            List<String> specialties,
            String guideType,
            String difficultyLevel,
            Double minPrice,
            Double maxPrice,
            Boolean featuredGuide,
            int page,
            int size,
            String sortBy,
            String sortDirection
    ) {
        Query query = new Query();

        // Strict constraints
        query.addCriteria(Criteria.where("isDeleted").is(false));
        query.addCriteria(Criteria.where("isAvailable").is(true));

        if (search != null && !search.trim().isEmpty()) {
            String searchRegex = ".*" + search.trim() + ".*";
            Criteria searchCriteria = new Criteria().orOperator(
                    Criteria.where("fullName").regex(searchRegex, "i"),
                    Criteria.where("specialties").regex(searchRegex, "i"),
                    Criteria.where("tags").regex(searchRegex, "i")
            );
            query.addCriteria(searchCriteria);
        }

        if (destinationIds != null && !destinationIds.isEmpty()) {
            query.addCriteria(Criteria.where("destinationIds").in(destinationIds));
        }

        if (languages != null && !languages.isEmpty()) {
            query.addCriteria(Criteria.where("languages").in(languages));
        }

        if (specialties != null && !specialties.isEmpty()) {
            query.addCriteria(Criteria.where("specialties").in(specialties));
        }

        if (guideType != null && !guideType.trim().isEmpty()) {
            query.addCriteria(Criteria.where("guideType").is(guideType.trim()));
        }

        if (difficultyLevel != null && !difficultyLevel.trim().isEmpty()) {
            query.addCriteria(Criteria.where("difficultyLevel").is(difficultyLevel.trim()));
        }

        if (featuredGuide != null) {
            query.addCriteria(Criteria.where("featuredGuide").is(featuredGuide));
        }

        if (minPrice != null || maxPrice != null) {
            Criteria priceCriteria = Criteria.where("basePrice");
            if (minPrice != null) priceCriteria.gte(minPrice);
            if (maxPrice != null) priceCriteria.lte(maxPrice);
            query.addCriteria(priceCriteria);
        }

        // Count total matched records before pagination slice
        long total = mongoTemplate.count(query, Guide.class);

        // Sorting
        Sort.Direction direction = "asc".equalsIgnoreCase(sortDirection) ? Sort.Direction.ASC : Sort.Direction.DESC;
        String sortField = (sortBy == null || sortBy.trim().isEmpty()) ? "averageRating" : sortBy.trim();
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));
        query.with(pageable);

        List<Guide> list = mongoTemplate.find(query, Guide.class);

        return PageResponse.from(new PageImpl<>(list, pageable, total));
    }

    // ─── HELPERS ─────────────────────────────────────────────────────────────

    private String generateUniqueSlug(String name) {
        String base = name.toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
        String slug = base;
        int count = 1;
        
        // Optimistic check: if collision, add count suffix
        while (guideRepository.findBySlug(slug).isPresent()) {
            slug = base + "-" + count++;
        }
        return slug;
    }

    private <T> List<T> safeList(List<T> input) {
        return input != null ? input : new ArrayList<>();
    }

    public void updateGuideReviews(String guideId, double averageRating, int reviewCount) {
        Guide guide = guideRepository.findById(guideId)
                .orElseThrow(() -> new RuntimeException("Guide not found"));
        guide.setAverageRating(averageRating);
        guide.setReviewCount(reviewCount);
        guide.setUpdatedAt(LocalDateTime.now());
        guideRepository.save(guide);
    }
}

