package com.example.backend.guider.repository;

import com.example.backend.guider.model.Guide;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GuideRepository extends MongoRepository<Guide, String> {
    
    Optional<Guide> findBySlug(String slug);

    boolean existsBySlug(String slug);

    List<Guide> findBySlugStartingWith(String prefix);
    
    Page<Guide> findByIsDeletedFalse(Pageable pageable);

    Page<Guide> findByDestinationIdsContainingAndIsAvailableTrueAndIsDeletedFalse(String destinationId, Pageable pageable);

    Page<Guide> findByIsAvailableTrueAndIsDeletedFalse(Pageable pageable);
}
