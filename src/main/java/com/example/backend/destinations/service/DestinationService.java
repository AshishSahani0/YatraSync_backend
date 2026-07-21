package com.example.backend.destinations.service;

import com.example.backend.destinations.dto.DestinationRequest;
import com.example.backend.destinations.dto.DestinationResponse;
import com.example.backend.destinations.mapper.DestinationMapper;
import com.example.backend.destinations.model.*;
import com.example.backend.destinations.repository.CategoryRepository;
import com.example.backend.destinations.repository.DestinationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


@Service
@RequiredArgsConstructor
public class DestinationService {

    private final DestinationRepository destinationRepository;
    private final CategoryRepository categoryRepository;

    // 🔒 Thread-safe in-memory cache to prevent unnecessary database hits on public list views
    private final List<DestinationResponse> approvedCache = new java.util.concurrent.CopyOnWriteArrayList<>();
    private final java.util.concurrent.atomic.AtomicBoolean isCacheValid = new java.util.concurrent.atomic.AtomicBoolean(false);

    private void invalidateCache() {
        isCacheValid.set(false);
        approvedCache.clear();
    }


    public DestinationResponse createDestination(DestinationRequest req, String adminId) {


        if (req.getName() == null || req.getName().isBlank()) {
            throw new RuntimeException("Destination name is required");
        }
        if (req.getLocation() == null) {
            throw new RuntimeException("Location is required");
        }


        String slug = generateUniqueSlug(req.getName());


        List<Category> categories = categoryRepository.findAllById(req.getCategoryIds());

        if (categories.size() != req.getCategoryIds().size()) {
            throw new RuntimeException("Some category IDs are invalid");
        }

        if (req.getLocation().getDisplayName() == null) {
            String display = String.join(", ",
                    safe(req.getLocation().getCity()),
                    safe(req.getLocation().getState()),
                    safe(req.getLocation().getCountry())
            ).replaceAll(",\\s*,", ",").replaceAll("^,\\s*|,\\s*$", "");

            req.getLocation().setDisplayName(display);
        }


        Destination destination = Destination.builder()
                // Core
                .name(req.getName().trim())
                .slug(slug)
                .description(req.getDescription())
                .location(req.getLocation())

                // Relations
                .categoryIds(safeList(req.getCategoryIds()))
                .tags(safeList(req.getTags()))

                // Media
                .images(safeList(req.getImages()))

                // Travel Content
                .bestTimeToVisit(req.getBestTimeToVisit())
                .weatherInfo(req.getWeatherInfo())
                .travelInfo(req.getTravelInfo())
                .routeMetadata(req.getRouteMetadata())
                .activities(safeList(req.getActivities()))

                // Pricing
                .costLevel(req.getCostLevel())

                // Stats (default)
                .averageRating(0.0)
                .totalReviews(0)
                .popularityScore(0)

                // Flags
                .isFeatured(Boolean.TRUE.equals(req.getIsFeatured()))
                .isDeleted(false)

                // Ownership
                .createdBy(adminId)
                .creatorType("ADMIN")

                // Moderation
                .status("APPROVED")

                // SEO (auto fallback)
                .metaTitle(req.getName())
                .metaDescription(req.getDescription())

                // Audit
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())

                .build();

        Destination saved = destinationRepository.save(destination);
        invalidateCache();


        return DestinationMapper.toResponse(saved, categories);
    }

    private String generateUniqueSlug(String name) {
        String base = name.toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");

        String slug = base;
        int count = 1;

        while (destinationRepository.findBySlug(slug).isPresent()) {
            slug = base + "-" + count++;
        }

        return slug;
    }

    private <T> List<T> safeList(List<T> list) {
        return list != null ? list : List.of();
    }
    private String safe(String value) {
        return value == null ? "" : value.trim();
    }

    public Page<DestinationResponse> getAllForAdmin(Pageable pageable) {

        // Apply default sorting if not provided
        Pageable safePageable = pageable.getSort().isSorted()
                ? pageable
                : PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by("createdAt").descending()
        );

        return destinationRepository.findByIsDeletedFalse(safePageable)
                .map(DestinationMapper::toResponse);
    }

    public DestinationResponse getByIdForAdmin(String id) {

        Destination destination = destinationRepository.findById(id)
                .filter(d -> !Boolean.TRUE.equals(d.getIsDeleted()))
                .orElseThrow(() -> new RuntimeException("Destination not found"));


        List<Category> categories =
                categoryRepository.findAllById(destination.getCategoryIds());

        return DestinationMapper.toResponse(destination, categories);
    }

    public DestinationResponse updateDestination(String id, DestinationRequest req, String adminId) {

        Destination destination = destinationRepository.findById(id)
                .filter(d -> !Boolean.TRUE.equals(d.getIsDeleted()))
                .orElseThrow(() -> new RuntimeException("Destination not found"));

        // ========================
        // ✅ NAME + SLUG
        // ========================
        if (req.getName() != null && !req.getName().isBlank()) {

            String newName = req.getName().trim();

            if (!newName.equals(destination.getName())) {
                destination.setName(newName);
                destination.setSlug(generateUniqueSlug(newName)); // 🔥 correct
            }
        }

        // ========================
        // ✅ DESCRIPTION
        // ========================
        if (req.getDescription() != null) {
            destination.setDescription(req.getDescription());
        }

        // ========================
        // ✅ LOCATION (MERGE SAFE)
        // ========================
        if (req.getLocation() != null) {

            if (destination.getLocation() == null) {
                destination.setLocation(req.getLocation());
            } else {

                Location existing = destination.getLocation();
                Location incoming = req.getLocation();

                // 🌍 BASIC FIELDS
                if (incoming.getCountry() != null)
                    existing.setCountry(incoming.getCountry());

                if (incoming.getState() != null)
                    existing.setState(incoming.getState());

                if (incoming.getCity() != null)
                    existing.setCity(incoming.getCity());

                if (incoming.getDisplayName() != null)
                    existing.setDisplayName(incoming.getDisplayName());

                // 📍 LAT / LNG
                if (incoming.getLatitude() != null)
                    existing.setLatitude(incoming.getLatitude());

                if (incoming.getLongitude() != null)
                    existing.setLongitude(incoming.getLongitude());

                // 🌐 GEO POINT DIRECT UPDATE
                if (incoming.getGeoPoint() != null) {
                    existing.setGeoPoint(incoming.getGeoPoint());
                }

                // 🔥 AUTO-SYNC GEO POINT (VERY IMPORTANT)
                if (existing.getLatitude() != null && existing.getLongitude() != null) {

                    existing.setGeoPoint(
                            GeoPoint.builder()
                                    .coordinates(List.of(
                                            existing.getLongitude(), // ⚠️ order: lng, lat
                                            existing.getLatitude()
                                    ))
                                    .build()
                    );
                }
            }
        }

        // ========================
        // ✅ CATEGORY
        // ========================
        if (req.getCategoryIds() != null) {

            List<String> incomingIds = req.getCategoryIds();

            // ✅ allow empty list → clear categories
            if (incomingIds.isEmpty()) {
                destination.setCategoryIds(List.of());
            } else {

                // 🔍 fetch only ACTIVE categories
                List<Category> categories =
                        categoryRepository.findAllById(incomingIds)
                                .stream()
                                .filter(cat -> Boolean.TRUE.equals(cat.getIsActive()))
                                .toList();

                // ❌ validation: missing or inactive IDs
                if (categories.size() != incomingIds.size()) {
                    throw new RuntimeException("Invalid or inactive category IDs");
                }

                // ✅ remove duplicates (important)
                List<String> uniqueIds = incomingIds.stream()
                        .distinct()
                        .toList();

                destination.setCategoryIds(uniqueIds);
            }
        }

        // ========================
        // ✅ SIMPLE LISTS
        // ========================
        if (req.getTags() != null) {
            destination.setTags(req.getTags());
        }

        if (req.getImages() != null) {

            List<Image> incoming = req.getImages();

            // ✅ clear images
            if (incoming.isEmpty()) {
                destination.setImages(List.of());
            } else {

                List<Image> cleaned = new ArrayList<>();

                boolean hasThumbnail = false;
                boolean hasCover = false;

                int index = 0;

                for (Image img : incoming) {

                    // ❌ skip invalid
                    if (img.getUrl() == null || img.getUrl().isBlank()) continue;

                    boolean isThumbnail = Boolean.TRUE.equals(img.getIsThumbnail());
                    boolean isCover = Boolean.TRUE.equals(img.getIsCover());

                    // 🔒 enforce single thumbnail
                    if (isThumbnail) {
                        if (hasThumbnail) isThumbnail = false;
                        else hasThumbnail = true;
                    }

                    // 🔒 enforce single cover
                    if (isCover) {
                        if (hasCover) isCover = false;
                        else hasCover = true;
                    }

                    Image clean = Image.builder()
                            .url(img.getUrl())
                            .caption(img.getCaption())
                            .altText(img.getAltText())
                            .order(img.getOrder() != null ? img.getOrder() : index)
                            .isThumbnail(isThumbnail)
                            .isCover(isCover)
                            .width(img.getWidth())
                            .height(img.getHeight())
                            .build();

                    cleaned.add(clean);
                    index++;
                }

                // 🔥 fallback (VERY IMPORTANT)
                if (!cleaned.isEmpty()) {

                    // ensure at least 1 cover
                    if (!hasCover) {
                        cleaned.get(0).setIsCover(true);
                    }

                    // ensure at least 1 thumbnail
                    if (!hasThumbnail) {
                        cleaned.get(0).setIsThumbnail(true);
                    }
                }

                // 📊 sort by order
                cleaned.sort(Comparator.comparing(img -> img.getOrder() != null ? img.getOrder() : 0));

                destination.setImages(cleaned);
            }
        }

        if (req.getActivities() != null) {

            List<Activity> incoming = req.getActivities();

            if (incoming.isEmpty()) {
                destination.setActivities(List.of()); // clear all
            } else {

                List<Activity> cleaned = incoming.stream()
                        .filter(a -> a.getName() != null && !a.getName().isBlank()) // basic validation
                        .map(a -> Activity.builder()
                                .name(a.getName())
                                .description(a.getDescription())
                                .type(a.getType())
                                .priceRange(a.getPriceRange())
                                .duration(a.getDuration())
                                .bestTime(a.getBestTime())
                                .isPopular(a.getIsPopular())
                                .imageUrl(a.getImageUrl())
                                .build()
                        )
                        .toList();

                destination.setActivities(cleaned);
            }
        }

        // ========================
        // ✅ BEST TIME (MERGE SAFE)
        // ========================
        if (req.getBestTimeToVisit() != null) {

            if (destination.getBestTimeToVisit() == null) {
                destination.setBestTimeToVisit(req.getBestTimeToVisit());
            } else {

                BestTime existing = destination.getBestTimeToVisit();
                BestTime incoming = req.getBestTimeToVisit();

                if (incoming.getStartMonth() != null)
                    existing.setStartMonth(incoming.getStartMonth());

                if (incoming.getEndMonth() != null)
                    existing.setEndMonth(incoming.getEndMonth());

                if (incoming.getSeason() != null)
                    existing.setSeason(incoming.getSeason());

                if (incoming.getNotes() != null)
                    existing.setNotes(incoming.getNotes());

                if (incoming.getHighlight() != null)
                    existing.setHighlight(incoming.getHighlight());

                if (incoming.getIsPeak() != null)
                    existing.setIsPeak(incoming.getIsPeak());
            }
        }

        // ========================
        // ✅ WEATHER (MERGE SAFE)
        // ========================
        if (req.getWeatherInfo() != null) {

            if (destination.getWeatherInfo() == null) {
                destination.setWeatherInfo(req.getWeatherInfo());
            } else {

                Weather existing = destination.getWeatherInfo();
                Weather incoming = req.getWeatherInfo();

                // 🌞 SUMMER
                if (incoming.getSummer() != null) {
                    existing.setSummer(
                            mergeSeason(existing.getSummer(), incoming.getSummer())
                    );
                }

                // ❄️ WINTER
                if (incoming.getWinter() != null) {
                    existing.setWinter(
                            mergeSeason(existing.getWinter(), incoming.getWinter())
                    );
                }

                // 🌧 MONSOON
                if (incoming.getMonsoon() != null) {
                    existing.setMonsoon(
                            mergeSeason(existing.getMonsoon(), incoming.getMonsoon())
                    );
                }

                // 🌸 SPRING (you forgot this earlier ❗)
                if (incoming.getSpring() != null) {
                    existing.setSpring(
                            mergeSeason(existing.getSpring(), incoming.getSpring())
                    );
                }

                // 🧾 SUMMARY
                if (incoming.getSummary() != null) {
                    existing.setSummary(incoming.getSummary());
                }

                // 📅 BEST MONTHS
                if (incoming.getBestMonths() != null) {
                    existing.setBestMonths(incoming.getBestMonths());
                }
            }
        }

        // ========================
        // TRAVEL INFO (MERGE SAFE)
        // ========================
        if (req.getTravelInfo() != null) {

            if (destination.getTravelInfo() == null) {
                destination.setTravelInfo(req.getTravelInfo());
            } else {

                TravelInfo existing = destination.getTravelInfo();
                TravelInfo incoming = req.getTravelInfo();

                if (incoming.getAirports() != null)
                    existing.setAirports(incoming.getAirports());

                if (incoming.getRailways() != null)
                    existing.setRailways(incoming.getRailways());

                if (incoming.getBusStations() != null)
                    existing.setBusStations(incoming.getBusStations());

                if (incoming.getRoadConnectivity() != null)
                    existing.setRoadConnectivity(incoming.getRoadConnectivity());

                if (incoming.getRoadNotes() != null)
                    existing.setRoadNotes(incoming.getRoadNotes());

                if (incoming.getTravelTips() != null)
                    existing.setTravelTips(incoming.getTravelTips());
            }
        }

        // ========================
        // ROUTE METADATA (MERGE SAFE)
        // ========================
        if (req.getRouteMetadata() != null) {

            if (destination.getRouteMetadata() == null) {
                destination.setRouteMetadata(req.getRouteMetadata());
            } else {

                RouteMetadata existing = destination.getRouteMetadata();
                RouteMetadata incoming = req.getRouteMetadata();

                if (incoming.getBestRoadRoute() != null)
                    existing.setBestRoadRoute(incoming.getBestRoadRoute());

                if (incoming.getNearestAirport() != null)
                    existing.setNearestAirport(incoming.getNearestAirport());

                if (incoming.getNearestRailway() != null)
                    existing.setNearestRailway(incoming.getNearestRailway());

                if (incoming.getPermitRequired() != null)
                    existing.setPermitRequired(incoming.getPermitRequired());

                if (incoming.getRoadDifficulty() != null)
                    existing.setRoadDifficulty(incoming.getRoadDifficulty());
            }
        }

        // ========================
        // ✅ COST
        // ========================
        if (req.getCostLevel() != null) {
            destination.setCostLevel(req.getCostLevel());
        }

        // ========================
        // ✅ FEATURE FLAG
        // ========================
        if (req.getIsFeatured() != null) {
            destination.setIsFeatured(req.getIsFeatured());
        }

        // ========================
        // ✅ SEO
        // ========================
        if (req.getName() != null) {
            destination.setMetaTitle(destination.getName());
        }

        if (req.getDescription() != null) {
            destination.setMetaDescription(destination.getDescription());
        }

        // ========================
        // FINAL SAVE
        // ========================
        destination.setUpdatedAt(LocalDateTime.now());

        Destination saved = destinationRepository.save(destination);
        invalidateCache();

        List<Category> categories =
                categoryRepository.findAllById(saved.getCategoryIds());

        return DestinationMapper.toResponse(saved, categories);
    }


    private SeasonWeather mergeSeason(SeasonWeather existing, SeasonWeather incoming) {

        if (existing == null) return incoming;

        if (incoming.getTemperature() != null)
            existing.setTemperature(incoming.getTemperature());

        if (incoming.getCondition() != null)
            existing.setCondition(incoming.getCondition());

        if (incoming.getDescription() != null)
            existing.setDescription(incoming.getDescription());

        if (incoming.getAvgHigh() != null)
            existing.setAvgHigh(incoming.getAvgHigh());

        if (incoming.getAvgLow() != null)
            existing.setAvgLow(incoming.getAvgLow());

        if (incoming.getIcon() != null)
            existing.setIcon(incoming.getIcon());

        return existing;
    }

    public List<DestinationResponse> getAllApproved() {
        if (!isCacheValid.get()) {
            synchronized (approvedCache) {
                if (!isCacheValid.get()) {
                    List<DestinationResponse> fresh = destinationRepository
                            .findByStatusAndIsDeletedFalseOrderByCreatedAtDesc("APPROVED")
                            .stream()
                            .map(DestinationMapper::toResponse)
                            .toList();
                    approvedCache.clear();
                    approvedCache.addAll(fresh);
                    isCacheValid.set(true);
                }
            }
        }
        return approvedCache;
    }

    public DestinationResponse getByIdApproved(String id) {

        Destination destination = destinationRepository.findById(id)
                .filter(d -> "APPROVED".equalsIgnoreCase(d.getStatus()))
                .filter(d -> !Boolean.TRUE.equals(d.getIsDeleted()))
                .orElseThrow(() -> new RuntimeException("Destination not available"));


        List<Category> categories =
                categoryRepository.findAllById(destination.getCategoryIds());

        return DestinationMapper.toResponse(destination, categories);
    }

    public DestinationResponse getBySlug(String slug) {

        Destination destination = destinationRepository.findBySlug(slug)
                .filter(d -> "APPROVED".equalsIgnoreCase(d.getStatus()))
                .filter(d -> !Boolean.TRUE.equals(d.getIsDeleted()))
                .orElseThrow(() -> new RuntimeException("Destination not found"));

        List<Category> categories =
                categoryRepository.findAllById(destination.getCategoryIds());

        return DestinationMapper.toResponse(destination, categories);
    }
    public void deleteDestination(String id) {

        Destination destination = destinationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Destination not found"));

        // 🔥 SOFT DELETE (RECOMMENDED)
        destination.setIsDeleted(true);
        destination.setStatus("DELETED");
        destination.setUpdatedAt(LocalDateTime.now());

        destinationRepository.save(destination);
        invalidateCache();
    }
    public void updateDestinationReviews(String destinationId, double averageRating, int totalReviews) {
        Destination destination = destinationRepository.findById(destinationId)
                .orElseThrow(() -> new RuntimeException("Destination not found"));
        destination.setAverageRating(averageRating);
        destination.setTotalReviews(totalReviews);
        destination.setUpdatedAt(LocalDateTime.now());
        destinationRepository.save(destination);
        invalidateCache();
    }
}
