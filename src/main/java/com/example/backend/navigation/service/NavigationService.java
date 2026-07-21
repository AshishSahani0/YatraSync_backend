package com.example.backend.navigation.service;

import com.example.backend.destinations.model.Destination;
import com.example.backend.destinations.repository.DestinationRepository;
import com.example.backend.guider.dto.GuideResponse;
import com.example.backend.guider.mapper.GuideMapper;
import com.example.backend.guider.repository.GuideRepository;
import com.example.backend.hotels.repository.HotelRepository;
import com.example.backend.hotels.mapper.HotelMapper;
import com.example.backend.hotels.dto.HotelResponse;
import com.example.backend.navigation.controller.NavigationController.CompactDestination;
import com.example.backend.navigation.model.RouteHistory;
import com.example.backend.navigation.repository.RouteHistoryRepository;
import com.example.backend.navigation.dto.RouteRequest;
import com.example.backend.navigation.dto.RouteResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NavigationService {

    private final DestinationRepository destinationRepository;
    private final HotelRepository hotelRepository;
    private final GuideRepository guideRepository;
    private final RouteHistoryRepository routeHistoryRepository;
    private final RestTemplate navigationRestTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public RouteResponse calculateRoute(RouteRequest req, String userId) {
        // 1. Fetch Target Destination coordinates
        Destination destination = destinationRepository.findById(req.getDestinationId())
                .filter(d -> !Boolean.TRUE.equals(d.getIsDeleted()))
                .orElseThrow(() -> new RuntimeException("Destination not found"));

        if (destination.getLocation() == null || 
            destination.getLocation().getLatitude() == null || 
            destination.getLocation().getLongitude() == null) {
            throw new RuntimeException("Destination does not have valid coordinates");
        }

        Double destLat = destination.getLocation().getLatitude();
        Double destLng = destination.getLocation().getLongitude();

        Double sourceLat = req.getSourceLat();
        Double sourceLng = req.getSourceLng();

        String mode = req.getTransportMode() != null ? req.getTransportMode().toLowerCase() : "driving";
        if (!mode.equals("driving") && !mode.equals("walking") && !mode.equals("cycling")) {
            mode = "driving";
        }

        // Map transit mode standard keywords to OSRM mode selectors
        String osrmMode;
        if (mode.equals("cycling")) {
            osrmMode = "bike"; // OSRM supports bike / car / foot natively on standard profiles
        } else if (mode.equals("walking")) {
            osrmMode = "foot";
        } else {
            osrmMode = "driving";
        }

        // 2. Fetch OSRM Route dynamically (Cached & Resilient with Timeout)
        OsrmRouteResult routeResult = fetchOsrmRoute(osrmMode, sourceLng, sourceLat, destLng, destLat);
        double distanceKm = routeResult.distanceKm;
        double durationMinutes = routeResult.durationMinutes;
        List<List<Double>> geometryCoords = routeResult.geometryCoords;

        // 3. Compute cost estimations tailored to transport mode
        double fuelCost = 0.0;
        double tollCost = 0.0;
        double taxiCost = 0.0;

        if (mode.equals("driving")) {
            // Petrol rates baseline ₹100/L. Average mileage 15km/L
            fuelCost = (distanceKm / 15.0) * 100.0;
            // Approximation of toll gates based on distance (>100km)
            tollCost = distanceKm > 100 ? (Math.floor(distanceKm / 120.0) * 150.0) : 0.0;
            // Taxi rate average ₹14/km + baseline fare
            taxiCost = 150.0 + (distanceKm * 14.0);
        }

        // 4. Fetch Stays recommendations (Hotels)
        Pageable stayLimit = PageRequest.of(0, 6);
        List<HotelResponse> stays = hotelRepository
                .findByDestinationIdAndIsActiveTrueAndIsDeletedFalse(destination.getId(), stayLimit)
                .map(HotelMapper::toResponse)
                .getContent();

        // 5. Fetch Local Certified Guides recommendations (Guider)
        Pageable guideLimit = PageRequest.of(0, 6);
        List<GuideResponse> guides = guideRepository
                .findByDestinationIdsContainingAndIsAvailableTrueAndIsDeletedFalse(destination.getId(), guideLimit)
                .map(GuideMapper::toResponse)
                .getContent();

        if (guides.isEmpty()) {
            // Fallback to active available guides if no direct destination link matches
            guides = guideRepository
                    .findByIsAvailableTrueAndIsDeletedFalse(guideLimit)
                    .map(GuideMapper::toResponse)
                    .getContent();
        }

        // 6. Build History Log
        RouteHistory history = RouteHistory.builder()
                .userId(userId) // Nullable if anonymous
                .sourceName(req.getSourceName() != null ? req.getSourceName().trim() : "Current Location Coords")
                .sourceLat(sourceLat)
                .sourceLng(sourceLng)
                .destinationId(destination.getId())
                .destinationName(destination.getName())
                .distanceKm(distanceKm)
                .durationMinutes(durationMinutes)
                .transportMode(mode)
                .searchedAt(LocalDateTime.now())
                .build();

        try {
            routeHistoryRepository.save(history);
        } catch (Exception e) {
            log.warn("Failed to log route search history to database: {}", e.getMessage());
        }

        // 7. Return enriched response object
        return RouteResponse.builder()
                .distanceKm(distanceKm)
                .durationMinutes(durationMinutes)
                .routeGeometry(geometryCoords)
                .transportMode(mode)
                .estimatedFuelCostInr(Math.round(fuelCost * 100.0) / 100.0)
                .estimatedTaxiCostInr(Math.round(taxiCost * 100.0) / 100.0)
                .estimatedTollsInr(Math.round(tollCost * 100.0) / 100.0)
                .destinationId(destination.getId())
                .destinationName(destination.getName())
                .destinationDescription(destination.getDescription())
                .routeMetadata(destination.getRouteMetadata())
                .activities(destination.getActivities() != null ? destination.getActivities() : Collections.emptyList())
                .recommendedStays(stays)
                .recommendedGuides(guides)
                .build();
    }

    @Cacheable(value = "compact_destinations")
    public List<CompactDestination> getCompactDestinations() {
        return destinationRepository.findApprovedCompactDestinations("APPROVED")
                .stream()
                .filter(d -> d.getLocation() != null && 
                            d.getLocation().getLatitude() != null && 
                            d.getLocation().getLongitude() != null)
                .map(d -> CompactDestination.builder()
                        .id(d.getId())
                        .name(d.getName())
                        .latitude(d.getLocation().getLatitude())
                        .longitude(d.getLocation().getLongitude())
                        .city(d.getLocation().getCity())
                        .country(d.getLocation().getCountry())
                        .build())
                .toList();
    }

    public List<RouteHistory> getSearchHistory(String userId) {
        if (userId == null) return Collections.emptyList();
        return routeHistoryRepository.findTop20ByUserIdOrderBySearchedAtDesc(userId);
    }

    @Cacheable(value = "osrm_routes", key = "#osrmMode + '-' + T(java.lang.Math).round(#sourceLng * 100) + '-' + T(java.lang.Math).round(#sourceLat * 100) + '-' + T(java.lang.Math).round(#destLng * 100) + '-' + T(java.lang.Math).round(#destLat * 100)")
    public OsrmRouteResult fetchOsrmRoute(String osrmMode, Double sourceLng, Double sourceLat, Double destLng, Double destLat) {
        String url = String.format(
                "http://router.project-osrm.org/route/v1/%s/%f,%f;%f,%f?overview=full&geometries=geojson",
                osrmMode, sourceLng, sourceLat, destLng, destLat
        );

        double distanceKm = 0.0;
        double durationMinutes = 0.0;
        List<List<Double>> geometryCoords = new ArrayList<>();

        try {
            log.info("Requesting route path from OSRM: {}", url);
            ResponseEntity<String> response = navigationRestTemplate.getForEntity(url, String.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode root = objectMapper.readTree(response.getBody());
                JsonNode routes = root.path("routes");
                if (routes.isArray() && !routes.isEmpty()) {
                    JsonNode primaryRoute = routes.get(0);
                    distanceKm = primaryRoute.path("distance").asDouble() / 1000.0;
                    durationMinutes = primaryRoute.path("duration").asDouble() / 60.0;

                    JsonNode coordsNode = primaryRoute.path("geometry").path("coordinates");
                    if (coordsNode.isArray()) {
                        for (JsonNode pNode : coordsNode) {
                            if (pNode.isArray() && pNode.size() >= 2) {
                                geometryCoords.add(List.of(pNode.get(0).asDouble(), pNode.get(1).asDouble()));
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("OSRM transit API failed or timed out. Performing Haversine fallback calculation", e);
            distanceKm = calculateHaversine(sourceLat, sourceLng, destLat, destLng);
            durationMinutes = (distanceKm / 60.0) * 60.0;
            geometryCoords.add(List.of(sourceLng, sourceLat));
            geometryCoords.add(List.of(destLng, destLat));
        }

        return new OsrmRouteResult(distanceKm, durationMinutes, geometryCoords);
    }

    private double calculateHaversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Earth radius in km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    public record OsrmRouteResult(double distanceKm, double durationMinutes, List<List<Double>> geometryCoords) {}
}
