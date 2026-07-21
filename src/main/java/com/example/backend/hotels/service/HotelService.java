package com.example.backend.hotels.service;

import com.example.backend.hotels.dto.HotelRequest;
import com.example.backend.hotels.dto.HotelResponse;
import com.example.backend.hotels.mapper.HotelMapper;
import com.example.backend.hotels.model.*;
import com.example.backend.hotels.repository.HotelRepository;
import com.example.backend.hotels.hotelreview.repository.HotelReviewRepository;
import com.example.backend.hotels.hotelreview.model.HotelReview;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HotelService {

    private final HotelRepository hotelRepository;
    private final HotelReviewRepository hotelReviewRepository;

    // ─── CREATE ──────────────────────────────────────────────────────────────

    public HotelResponse createHotel(HotelRequest req, String adminId) {

        if (req.getName() == null || req.getName().isBlank()) {
            throw new RuntimeException("Hotel name is required");
        }
        if (req.getDestinationId() == null || req.getDestinationId().isBlank()) {
            throw new RuntimeException("Destination ID is required");
        }

        String slug = generateUniqueSlug(req.getName());

        Hotel hotel = Hotel.builder()
                // Basic
                .name(req.getName().trim())
                .slug(slug)
                .description(req.getDescription())
                .shortDescription(req.getShortDescription())
                .hotelType(req.getHotelType())
                // Destination
                .destinationId(req.getDestinationId())
                // Location
                .location(req.getLocation())
                // Media
                .images(safe(req.getImages()))
                // Amenities
                .amenities(safe(req.getAmenities()))
                // Rooms
                .rooms(safe(req.getRooms()))
                // Pricing
                .priceRange(req.getPriceRange())
                .averageNightPrice(req.getAverageNightPrice())
                .taxIncluded(req.getTaxIncluded())
                .extraGuestPrice(req.getExtraGuestPrice())
                // Policy
                .policy(req.getPolicy())
                // Ratings (default)
                .starRating(req.getStarRating())
                .adminAverageRating(req.getAdminAverageRating() != null ? req.getAdminAverageRating() : 0.0)
                .adminTotalReviews(req.getAdminTotalReviews() != null ? req.getAdminTotalReviews() : 0)
                .averageRating(req.getAdminAverageRating() != null ? req.getAdminAverageRating() : 0.0)
                .totalReviews(req.getAdminTotalReviews() != null ? req.getAdminTotalReviews() : 0)
                // Transport
                .nearestAirport(req.getNearestAirport())
                .nearestRailway(req.getNearestRailway())
                .nearestBusStop(req.getNearestBusStop())
                // Nearby
                .nearbyPlaces(safe(req.getNearbyPlaces()))
                // Food
                .restaurantAvailable(req.getRestaurantAvailable())
                .breakfastIncluded(req.getBreakfastIncluded())
                .mealOptions(safe(req.getMealOptions()))
                .cuisineTypes(safe(req.getCuisineTypes()))
                // Availability
                .isActive(Boolean.TRUE.equals(req.getIsActive()))
                .isFullyBooked(Boolean.FALSE.equals(req.getIsFullyBooked()) ? false : Boolean.TRUE.equals(req.getIsFullyBooked()))
                .seasonalAvailability(req.getSeasonalAvailability())
                // Discovery
                .isFeatured(Boolean.TRUE.equals(req.getIsFeatured()))
                .isTrending(Boolean.TRUE.equals(req.getIsTrending()))
                .popularityScore(0)
                // Contact
                .phone(req.getPhone())
                .email(req.getEmail())
                .website(req.getWebsite())
                .whatsapp(req.getWhatsapp())
                // Safety
                .safety(req.getSafety())
                // SEO
                .metaTitle(req.getMetaTitle() != null ? req.getMetaTitle() : req.getName())
                .metaDescription(req.getMetaDescription() != null ? req.getMetaDescription() : req.getShortDescription())
                .tags(safe(req.getTags()))
                // Audit
                .createdBy(adminId)
                .isDeleted(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return HotelMapper.toResponse(hotelRepository.save(hotel));
    }

    // ─── READ (ADMIN) ─────────────────────────────────────────────────────────

    public Page<HotelResponse> getAllForAdmin(Pageable pageable) {
        Pageable safe = ensureSorted(pageable, "createdAt");
        return hotelRepository.findByIsDeletedFalse(safe)
                .map(HotelMapper::toResponse);
    }

    public Page<HotelResponse> getByDestinationForAdmin(String destinationId, Pageable pageable) {
        Pageable safe = ensureSorted(pageable, "createdAt");
        return hotelRepository.findByDestinationIdAndIsDeletedFalse(destinationId, safe)
                .map(HotelMapper::toResponse);
    }

    public HotelResponse getByIdForAdmin(String id) {
        Hotel h = hotelRepository.findById(id)
                .filter(hotel -> !Boolean.TRUE.equals(hotel.getIsDeleted()))
                .orElseThrow(() -> new RuntimeException("Hotel not found"));
        return HotelMapper.toResponse(h);
    }

    // ─── READ (PUBLIC) ────────────────────────────────────────────────────────

    public Page<HotelResponse> getPublicByDestination(String destinationId, Pageable pageable) {
        Pageable safe = ensureSorted(pageable, "popularityScore");
        return hotelRepository.findByDestinationIdAndIsActiveTrueAndIsDeletedFalse(destinationId, safe)
                .map(HotelMapper::toResponse);
    }

    public Page<HotelResponse> getAllPublic(Pageable pageable) {
        Pageable safe = ensureSorted(pageable, "popularityScore");
        return hotelRepository.findByIsActiveTrueAndIsDeletedFalse(safe)
                .map(HotelMapper::toResponse);
    }

    public HotelResponse getBySlugPublic(String slug) {
        Hotel h = hotelRepository.findBySlug(slug)
                .filter(hotel -> Boolean.TRUE.equals(hotel.getIsActive()))
                .filter(hotel -> !Boolean.TRUE.equals(hotel.getIsDeleted()))
                .orElseThrow(() -> new RuntimeException("Hotel not found or unavailable"));
        return HotelMapper.toResponse(h);
    }

    // ─── UPDATE ───────────────────────────────────────────────────────────────

    public HotelResponse updateHotel(String id, HotelRequest req, String adminId) {

        Hotel hotel = hotelRepository.findById(id)
                .filter(h -> !Boolean.TRUE.equals(h.getIsDeleted()))
                .orElseThrow(() -> new RuntimeException("Hotel not found"));

        // Basic
        if (req.getName() != null && !req.getName().isBlank()) {
            String newName = req.getName().trim();
            if (!newName.equals(hotel.getName())) {
                hotel.setName(newName);
                hotel.setSlug(generateUniqueSlug(newName));
            }
        }
        if (req.getDescription()      != null) hotel.setDescription(req.getDescription());
        if (req.getShortDescription() != null) hotel.setShortDescription(req.getShortDescription());
        if (req.getHotelType()        != null) hotel.setHotelType(req.getHotelType());

        // Destination
        if (req.getDestinationId() != null && !req.getDestinationId().isBlank()) {
            hotel.setDestinationId(req.getDestinationId());
        }

        // Location
        if (req.getLocation() != null) hotel.setLocation(req.getLocation());

        // Media
        if (req.getImages() != null) hotel.setImages(req.getImages());

        // Amenities
        if (req.getAmenities() != null) hotel.setAmenities(req.getAmenities());

        // Rooms
        if (req.getRooms() != null) hotel.setRooms(req.getRooms());

        // Pricing
        if (req.getPriceRange()        != null) hotel.setPriceRange(req.getPriceRange());
        if (req.getAverageNightPrice() != null) hotel.setAverageNightPrice(req.getAverageNightPrice());
        if (req.getTaxIncluded()       != null) hotel.setTaxIncluded(req.getTaxIncluded());
        if (req.getExtraGuestPrice()   != null) hotel.setExtraGuestPrice(req.getExtraGuestPrice());

        // Policy
        if (req.getPolicy() != null) hotel.setPolicy(req.getPolicy());

        // Star rating
        if (req.getStarRating() != null) hotel.setStarRating(req.getStarRating());

        // Transport
        if (req.getNearestAirport() != null) hotel.setNearestAirport(req.getNearestAirport());
        if (req.getNearestRailway() != null) hotel.setNearestRailway(req.getNearestRailway());
        if (req.getNearestBusStop() != null) hotel.setNearestBusStop(req.getNearestBusStop());

        // Nearby
        if (req.getNearbyPlaces() != null) hotel.setNearbyPlaces(req.getNearbyPlaces());

        // Food
        if (req.getRestaurantAvailable() != null) hotel.setRestaurantAvailable(req.getRestaurantAvailable());
        if (req.getBreakfastIncluded()   != null) hotel.setBreakfastIncluded(req.getBreakfastIncluded());
        if (req.getMealOptions()         != null) hotel.setMealOptions(req.getMealOptions());
        if (req.getCuisineTypes()        != null) hotel.setCuisineTypes(req.getCuisineTypes());

        // Availability
        if (req.getIsActive()            != null) hotel.setIsActive(req.getIsActive());
        if (req.getIsFullyBooked()       != null) hotel.setIsFullyBooked(req.getIsFullyBooked());
        if (req.getSeasonalAvailability() != null) hotel.setSeasonalAvailability(req.getSeasonalAvailability());

        // Discovery
        if (req.getIsFeatured() != null) hotel.setIsFeatured(req.getIsFeatured());
        if (req.getIsTrending() != null) hotel.setIsTrending(req.getIsTrending());

        // Contact
        if (req.getPhone()    != null) hotel.setPhone(req.getPhone());
        if (req.getEmail()    != null) hotel.setEmail(req.getEmail());
        if (req.getWebsite()  != null) hotel.setWebsite(req.getWebsite());
        if (req.getWhatsapp() != null) hotel.setWhatsapp(req.getWhatsapp());

        // Safety
        if (req.getSafety() != null) hotel.setSafety(req.getSafety());

        // SEO
        if (req.getMetaTitle()       != null) hotel.setMetaTitle(req.getMetaTitle());
        if (req.getMetaDescription() != null) hotel.setMetaDescription(req.getMetaDescription());
        if (req.getTags()            != null) hotel.setTags(req.getTags());

        boolean ratingsChanged = false;
        if (req.getAdminAverageRating() != null) {
            hotel.setAdminAverageRating(req.getAdminAverageRating());
            ratingsChanged = true;
        }
        if (req.getAdminTotalReviews() != null) {
            hotel.setAdminTotalReviews(req.getAdminTotalReviews());
            ratingsChanged = true;
        }

        hotel.setUpdatedAt(LocalDateTime.now());
        Hotel saved = hotelRepository.save(hotel);

        if (ratingsChanged) {
            recalculateAndSaveAverageRating(saved.getId());
            saved = hotelRepository.findById(saved.getId()).orElse(saved);
        }

        return HotelMapper.toResponse(saved);
    }

    // ─── DELETE (SOFT) ────────────────────────────────────────────────────────

    public void deleteHotel(String id) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hotel not found"));
        hotel.setIsDeleted(true);
        hotel.setIsActive(false);
        hotel.setUpdatedAt(LocalDateTime.now());
        hotelRepository.save(hotel);
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private String generateUniqueSlug(String name) {
        String base = name.toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
        String slug = base;
        int count = 1;
        while (hotelRepository.findBySlug(slug).isPresent()) {
            slug = base + "-" + count++;
        }
        return slug;
    }

    private <T> List<T> safe(List<T> list) {
        return list != null ? list : Collections.emptyList();
    }

    private Pageable ensureSorted(Pageable pageable, String defaultField) {
        return pageable.getSort().isSorted()
                ? pageable
                : PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                        Sort.by(defaultField).descending());
    }
    public void updateHotelReviews(String hotelId, double averageRating, int totalReviews) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new RuntimeException("Hotel not found"));
        hotel.setAverageRating(averageRating);
        hotel.setTotalReviews(totalReviews);
        hotel.setUpdatedAt(LocalDateTime.now());
        hotelRepository.save(hotel);
    }

    public Hotel getHotelById(String hotelId) {
        return hotelRepository.findById(hotelId)
                .orElseThrow(() -> new RuntimeException("Hotel not found"));
    }

    public void recalculateAndSaveAverageRating(String hotelId) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new RuntimeException("Hotel not found"));
        List<HotelReview> reviews = hotelReviewRepository.findByHotelId(hotelId);

        double adminAvg = hotel.getAdminAverageRating() != null ? hotel.getAdminAverageRating() : 0.0;
        int adminCount = hotel.getAdminTotalReviews() != null ? hotel.getAdminTotalReviews() : 0;

        int userCount = reviews.size();
        int totalReviews = adminCount + userCount;
        double averageRating = 0.0;

        if (totalReviews > 0) {
            double sum = adminAvg * adminCount;
            for (HotelReview r : reviews) {
                sum += r.getRating();
            }
            averageRating = sum / totalReviews;
            // Round to 1 decimal place
            averageRating = Math.round(averageRating * 10.0) / 10.0;
        }

        hotel.setAverageRating(averageRating);
        hotel.setTotalReviews(totalReviews);
        hotel.setUpdatedAt(LocalDateTime.now());
        hotelRepository.save(hotel);
    }
}
