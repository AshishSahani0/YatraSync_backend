package com.example.backend.hotels.repository;

import com.example.backend.hotels.model.Hotel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface HotelRepository extends MongoRepository<Hotel, String> {

    /** Used by slug uniqueness checker during create/update */
    Optional<Hotel> findBySlug(String slug);

    /** Admin paginated list — non-deleted only */
    Page<Hotel> findByIsDeletedFalse(Pageable pageable);

    /** Admin list filtered by destination */
    Page<Hotel> findByDestinationIdAndIsDeletedFalse(String destinationId, Pageable pageable);

    /** Public: active + non-deleted by destination */
    Page<Hotel> findByDestinationIdAndIsActiveTrueAndIsDeletedFalse(
            String destinationId, Pageable pageable);

    /** Public: all active non-deleted */
    Page<Hotel> findByIsActiveTrueAndIsDeletedFalse(Pageable pageable);

    /** Count rooms for fast availability checks */
    long countByDestinationIdAndIsActiveTrueAndIsDeletedFalse(String destinationId);
}
