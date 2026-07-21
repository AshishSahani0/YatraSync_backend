package com.example.backend.navigation.repository;

import com.example.backend.navigation.model.RouteHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface RouteHistoryRepository extends MongoRepository<RouteHistory, String> {

    /** Find top 20 latest search logs for a specific user ordered by time */
    List<RouteHistory> findTop20ByUserIdOrderBySearchedAtDesc(String userId);

    /** Find search logs for a specific user ordered by time */
    List<RouteHistory> findByUserIdOrderBySearchedAtDesc(String userId);

    /** Paginated search logs for a specific user */
    Page<RouteHistory> findByUserId(String userId, Pageable pageable);
}
