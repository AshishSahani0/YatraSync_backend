package com.example.backend.hotels.hotelreview.repository;

import com.example.backend.hotels.hotelreview.model.HotelReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.example.backend.common.dto.ReviewStats;
import org.springframework.data.mongodb.repository.Aggregation;
import java.util.List;
import java.util.Optional;

public interface HotelReviewRepository extends MongoRepository<HotelReview, String> {
    
    List<HotelReview> findByHotelId(String hotelId);
    
    Page<HotelReview> findByHotelIdOrderByCreatedAtDesc(String hotelId, Pageable pageable);
    
    Optional<HotelReview> findByHotelIdAndUserId(String hotelId, String userId);
    
    void deleteByHotelIdAndUserId(String hotelId, String userId);
    
    long countByHotelId(String hotelId);

    @Aggregation(pipeline = {
        "{ '$match': { 'hotelId': ?0 } }",
        "{ '$group': { '_id': null, 'averageRating': { '$avg': '$rating' }, 'totalReviews': { '$sum': 1 } } }"
    })
    Optional<ReviewStats> getReviewStatsByHotelId(String hotelId);
}
