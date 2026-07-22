package com.example.backend.destinations.getDestination;

import com.example.backend.destinations.model.Destination;
import com.example.backend.destinations.repository.DestinationRepository;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import org.springframework.data.mongodb.core.query.TextCriteria;
import java.util.List;
import java.util.Optional;

@Service
public class GetDestinationService {

    private final MongoTemplate mongoTemplate;
    private final DestinationRepository destinationRepository;

    public GetDestinationService(MongoTemplate mongoTemplate, DestinationRepository destinationRepository) {
        this.mongoTemplate = mongoTemplate;
        this.destinationRepository = destinationRepository;
    }

    /**
     * Get approved, non-deleted destination by slug for public users
     */
    public Optional<Destination> getDestinationBySlug(String slug) {
        return destinationRepository.findBySlug(slug)
                .filter(d -> "APPROVED".equalsIgnoreCase(d.getStatus()))
                .filter(d -> !Boolean.TRUE.equals(d.getIsDeleted()));
    }

    /**
     * Get approved, non-deleted destination by ID for public users
     */
    public Optional<Destination> getDestinationById(String id) {
        return destinationRepository.findById(id)
                .filter(d -> "APPROVED".equalsIgnoreCase(d.getStatus()))
                .filter(d -> !Boolean.TRUE.equals(d.getIsDeleted()));
    }

    /**
     * Dynamic index-backed filtering of destinations with pagination.
     * Returns a stable {@link PageResponse} DTO instead of {@code PageImpl}
     * to guarantee a consistent JSON structure on the wire.
     */
    public PageResponse<Destination> getFilteredDestinations(DestinationFilterRequest filter) {
        Query query = new Query();

        // 🛡️ Strict security constraint: only approved & non-deleted items
        query.addCriteria(Criteria.where("status").is("APPROVED"));
        query.addCriteria(Criteria.where("isDeleted").is(false));

        // 🔍 Search text matching using text index (O(log N) complexity)
        if (filter.getSearch() != null && !filter.getSearch().trim().isEmpty()) {
            query.addCriteria(TextCriteria.forDefaultLanguage().matching(filter.getSearch().trim()));
        }

        // 🏷️ Filter by categories list
        if (filter.getCategoryIds() != null && !filter.getCategoryIds().isEmpty()) {
            query.addCriteria(Criteria.where("categoryIds").in(filter.getCategoryIds()));
        }

        // 💸 Filter by budget cost levels
        if (filter.getCostLevel() != null && !filter.getCostLevel().trim().isEmpty()) {
            query.addCriteria(Criteria.where("costLevel").is(filter.getCostLevel().trim().toUpperCase()));
        }

        // ⭐ Filter by featured status
        if (filter.getIsFeatured() != null) {
            query.addCriteria(Criteria.where("isFeatured").is(filter.getIsFeatured()));
        }

        // 🌦️ Filter by peak season
        if (filter.getSeason() != null && !filter.getSeason().trim().isEmpty()) {
            query.addCriteria(Criteria.where("bestTimeToVisit.season").regex(".*" + filter.getSeason().trim() + ".*", "i"));
        }

        // ⭐ Filter by minimum rating
        if (filter.getMinRating() != null && filter.getMinRating() > 0.0) {
            query.addCriteria(Criteria.where("averageRating").gte(filter.getMinRating()));
        }

        // 🧮 Count total matched documents (before pagination)
        long total = mongoTemplate.count(query, Destination.class);

        // 🔀 Apply sorting and direction
        Sort.Direction direction = "asc".equalsIgnoreCase(filter.getSortDirection())
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        String sortByField = filter.getSortBy();
        if (sortByField == null || sortByField.trim().isEmpty()) {
            sortByField = "popularityScore";
        }

        Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize(), Sort.by(direction, sortByField));
        query.with(pageable);

        // 🏷️ Optimize payload: project only required fields for list view
        query.fields().include("id", "name", "slug", "location", "categoryIds", "tags", "images", "costLevel", "averageRating", "totalReviews", "popularityScore", "isFeatured");

        // 📦 Fetch current page slice
        List<Destination> list = mongoTemplate.find(query, Destination.class);

        // ✅ Wrap in stable DTO — avoids unstable PageImpl serialization warning
        return PageResponse.from(new PageImpl<>(list, pageable, total));
    }
}
