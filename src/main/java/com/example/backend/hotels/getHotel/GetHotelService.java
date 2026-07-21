package com.example.backend.hotels.getHotel;

import com.example.backend.hotels.dto.HotelResponse;
import com.example.backend.hotels.mapper.HotelMapper;
import com.example.backend.hotels.model.Hotel;
import com.example.backend.hotels.repository.HotelRepository;
import com.example.backend.destinations.getDestination.PageResponse;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetHotelService {

    private final MongoTemplate mongoTemplate;
    private final HotelRepository hotelRepository;

    public GetHotelService(MongoTemplate mongoTemplate, HotelRepository hotelRepository) {
        this.mongoTemplate = mongoTemplate;
        this.hotelRepository = hotelRepository;
    }

    /**
     * Dynamic index-backed filtering of hotels with pagination.
     * Returns a stable {@link PageResponse} of {@link HotelResponse} DTO.
     */
    public PageResponse<HotelResponse> getFilteredHotels(HotelFilterRequest filter) {
        Query query = new Query();

        // 🛡️ Strict security constraint: only active & non-deleted items
        query.addCriteria(Criteria.where("isActive").is(true));
        query.addCriteria(Criteria.where("isDeleted").is(false));

        // 🔍 Search text matching name, description, landmark, or city
        if (filter.getSearch() != null && !filter.getSearch().trim().isEmpty()) {
            String searchRegex = ".*" + filter.getSearch().trim() + ".*";
            Criteria searchCriteria = new Criteria().orOperator(
                    Criteria.where("name").regex(searchRegex, "i"),
                    Criteria.where("description").regex(searchRegex, "i"),
                    Criteria.where("location.city").regex(searchRegex, "i"),
                    Criteria.where("location.landmark").regex(searchRegex, "i")
            );
            query.addCriteria(searchCriteria);
        }

        // 🗺️ Destination filter
        if (filter.getDestinationId() != null && !filter.getDestinationId().trim().isEmpty() && !"ALL".equalsIgnoreCase(filter.getDestinationId())) {
            query.addCriteria(Criteria.where("destinationId").is(filter.getDestinationId().trim()));
        }

        // 🏨 Hotel Type filter
        if (filter.getHotelType() != null && !filter.getHotelType().trim().isEmpty() && !"ALL".equalsIgnoreCase(filter.getHotelType())) {
            query.addCriteria(Criteria.where("hotelType").is(filter.getHotelType().trim()));
        }

        // ⭐ Star Rating filter
        if (filter.getStarRating() != null) {
            query.addCriteria(Criteria.where("starRating").is(filter.getStarRating()));
        }

        // 💑 Couple Friendly filter
        if (Boolean.TRUE.equals(filter.getCoupleFriendly())) {
            query.addCriteria(Criteria.where("policy.coupleFriendly").is(true));
        }

        // 🥞 Breakfast Included filter
        if (Boolean.TRUE.equals(filter.getBreakfastIncluded())) {
            query.addCriteria(Criteria.where("breakfastIncluded").is(true));
        }

        // ⭐ User Average Rating filter
        if (filter.getMinRating() != null && filter.getMinRating() > 0.0) {
            query.addCriteria(Criteria.where("averageRating").gte(filter.getMinRating()));
        }

        // 💵 Price range filter
        if (filter.getMinPrice() != null || filter.getMaxPrice() != null) {
            Criteria priceCriteria = Criteria.where("averageNightPrice");
            if (filter.getMinPrice() != null) {
                priceCriteria = priceCriteria.gte(filter.getMinPrice());
            }
            if (filter.getMaxPrice() != null) {
                priceCriteria = priceCriteria.lte(filter.getMaxPrice());
            }
            query.addCriteria(priceCriteria);
        }

        // 🧮 Count total matched documents (before pagination)
        long total = mongoTemplate.count(query, Hotel.class);

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

        // 📦 Fetch current page slice
        List<Hotel> list = mongoTemplate.find(query, Hotel.class);

        // 🔁 Map to response DTOs
        Page<HotelResponse> pageResult = new PageImpl<>(list, pageable, total)
                .map(HotelMapper::toResponse);

        // ✅ Wrap in stable DTO
        return PageResponse.from(pageResult);
    }
}
