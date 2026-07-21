package com.example.backend.guider.guiderreviews.repository;

import com.example.backend.guider.guiderreviews.model.GuideReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface GuideReviewRepository extends MongoRepository<GuideReview, String> {
    
    List<GuideReview> findByGuideId(String guideId);
    
    Page<GuideReview> findByGuideIdOrderByCreatedAtDesc(String guideId, Pageable pageable);
    
    Optional<GuideReview> findByGuideIdAndUserId(String guideId, String userId);
    
    void deleteByGuideIdAndUserId(String guideId, String userId);
    
    long countByGuideId(String guideId);
}
