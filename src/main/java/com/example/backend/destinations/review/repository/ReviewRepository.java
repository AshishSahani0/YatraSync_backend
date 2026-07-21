package com.example.backend.destinations.review.repository;

import com.example.backend.destinations.review.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends MongoRepository<Review, String> {
    
    List<Review> findByDestinationId(String destinationId);
    
    Page<Review> findByDestinationIdOrderByCreatedAtDesc(String destinationId, Pageable pageable);
    
    Optional<Review> findByDestinationIdAndUserId(String destinationId, String userId);
    
    void deleteByDestinationIdAndUserId(String destinationId, String userId);
    
    long countByDestinationId(String destinationId);
}
