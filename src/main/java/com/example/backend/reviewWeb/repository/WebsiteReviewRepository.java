package com.example.backend.reviewWeb.repository;

import com.example.backend.reviewWeb.model.WebsiteReview;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface WebsiteReviewRepository extends MongoRepository<WebsiteReview, String> {
    List<WebsiteReview> findTop10ByOrderByCreatedAtDesc();
}
