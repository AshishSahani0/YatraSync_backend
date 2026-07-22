package com.example.backend.destinations.repository;

import com.example.backend.destinations.model.Destination;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface DestinationRepository extends MongoRepository<Destination, String> {


    Optional<Destination> findBySlug(String slug);

    boolean existsBySlug(String slug);

    List<Destination> findBySlugStartingWith(String prefix);



    Page<Destination> findByIsDeletedFalse(Pageable pageable);



    List<Destination> findByStatusAndIsDeletedFalseOrderByCreatedAtDesc(String status);

    @org.springframework.data.mongodb.repository.Query(value = "{ 'status': ?0, 'isDeleted': false, 'location.latitude': { $ne: null }, 'location.longitude': { $ne: null } }", fields = "{ 'id': 1, 'name': 1, 'location': 1 }")
    List<Destination> findApprovedCompactDestinations(String status);
}
