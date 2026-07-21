package com.example.backend.destinations.controller;

import com.example.backend.destinations.dto.DestinationResponse;
import com.example.backend.destinations.getDestination.DestinationFilterRequest;
import com.example.backend.destinations.getDestination.GetDestinationService;
import com.example.backend.destinations.getDestination.PageResponse;
import com.example.backend.destinations.service.DestinationService;
import com.example.backend.destinations.model.Destination;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/public/destinations")
@RequiredArgsConstructor
public class PublicDestinationController {

    private final DestinationService service;
    private final GetDestinationService getDestinationService;

    /**
     * Public pageable filter search endpoint.
     * Accepts optional query params for search, category, cost, season, and pagination.
     * When no filter params are supplied it returns all approved destinations sorted by popularity.
     */
    @GetMapping
    public PageResponse<Destination> getPublicDestinations(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) List<String> categoryIds,
            @RequestParam(required = false) String costLevel,
            @RequestParam(required = false) Boolean isFeatured,
            @RequestParam(required = false) String season,
            @RequestParam(required = false) Double minRating,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "popularityScore") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        DestinationFilterRequest request = DestinationFilterRequest.builder()
                .search(search)
                .categoryIds(categoryIds)
                .costLevel(costLevel)
                .isFeatured(isFeatured)
                .season(season)
                .minRating(minRating)
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .build();

        return getDestinationService.getFilteredDestinations(request);
    }

    /**
     * Public details retrieval by unique ID.
     */
    @GetMapping("/{id}")
    public DestinationResponse getById(@PathVariable String id) {
        return service.getByIdApproved(id);
    }

    /**
     * Public details retrieval by unique slug.
     */
    @GetMapping("/slug/{slug}")
    public DestinationResponse getBySlug(@PathVariable String slug) {
        return service.getBySlug(slug);
    }
}
